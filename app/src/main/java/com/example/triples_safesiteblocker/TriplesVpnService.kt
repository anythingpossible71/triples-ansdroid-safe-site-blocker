package com.example.triples_safesiteblocker

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.*
import androidx.room.Room
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class TriplesVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnJob: Job? = null
    private lateinit var blockedHosts: Set<String>
    private var httpServer: LocalBlockHttpServer? = null
    private var dnsServer: TriplesDnsServer? = null
    private var dnsForwardJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("TriplesVpnService", "onStartCommand: intent received, action=${intent?.action}")
        if (intent?.action == "STOP_VPN") {
            Log.i("TriplesVpnService", "Received STOP_VPN action, closing VPN interface and stopping service")
            vpnJob?.cancel()
            dnsForwardJob?.cancel()
            vpnInterface?.close()
            vpnInterface = null
            httpServer?.stop()
            httpServer = null
            dnsServer?.stopServer()
            dnsServer = null
            stopSelf()
            return START_NOT_STICKY
        }
        Log.i("TriplesVpnService", "VPN service started")
        val builder = Builder()
            .setSession("TriplesVPN")
            .addAddress("10.0.0.2", 32)
            .addDnsServer("8.8.8.8") // Use public DNS for compatibility
            .addRoute("8.8.8.8", 32) // Only DNS goes through VPN
        vpnInterface = builder.establish()

        // Start local HTTP server on port 8080
        if (httpServer == null) {
            httpServer = LocalBlockHttpServer(applicationContext)
            httpServer?.start()
        }

        // Load blocked hosts from Room and start DNS server on port 53535
        vpnJob = CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                WebsiteDatabase::class.java,
                "website_database"
            ).build()
            blockedHosts = db.websiteDao().getAll().filter { it.isBlocked }.map { it.url }.toSet()
            if (dnsServer == null) {
                dnsServer = TriplesDnsServer(blockedHosts)
                dnsServer?.start()
            }
        }

        // Start DNS packet interception/forwarding
        dnsForwardJob = CoroutineScope(Dispatchers.IO).launch {
            interceptAndForwardDnsPackets()
        }
        return START_STICKY
    }

    private fun interceptPackets() {
        // TODO: Read packets from vpnInterface?.fileDescriptor
        // Parse TCP/UDP packets, extract destination host
        // If host in blockedHosts, serve local HTML (blocked.html)
        // Otherwise, forward traffic as normal
        // This is a complex task and requires a TCP/IP stack or library
        Log.i("TriplesVpnService", "Packet interception loop started (not yet implemented)")
    }

    private suspend fun interceptAndForwardDnsPackets() = withContext(Dispatchers.IO) {
        val fd = vpnInterface?.fileDescriptor ?: return@withContext
        val input = FileInputStream(fd)
        val output = FileOutputStream(fd)
        val buf = ByteArray(32767)
        while (true) {
            val len = try { input.read(buf) } catch (e: Exception) { break }
            if (len <= 0) continue
            // Parse UDP packet (very basic, assumes IPv4/UDP)
            if (len < 28) continue // Minimum size for IPv4+UDP+DNS
            val ipProto = buf[9].toInt() and 0xFF
            if (ipProto != 17) continue // Not UDP
            val srcPort = ((buf[20].toInt() and 0xFF) shl 8) or (buf[21].toInt() and 0xFF)
            val dstPort = ((buf[22].toInt() and 0xFF) shl 8) or (buf[23].toInt() and 0xFF)
            if (dstPort != 53) continue // Not DNS
            val udpLen = ((buf[24].toInt() and 0xFF) shl 8) or (buf[25].toInt() and 0xFF)
            val dnsPayloadOffset = 28
            val dnsPayloadLen = udpLen - 8
            if (dnsPayloadOffset + dnsPayloadLen > len) continue
            val dnsPayload = buf.copyOfRange(dnsPayloadOffset, dnsPayloadOffset + dnsPayloadLen)
            // Forward DNS payload to local DNS server
            val dnsSocket = DatagramSocket()
            val dnsPacket = DatagramPacket(dnsPayload, dnsPayload.size, InetAddress.getByName("127.0.0.1"), 53535)
            dnsSocket.send(dnsPacket)
            val respBuf = ByteArray(512)
            val respPacket = DatagramPacket(respBuf, respBuf.size)
            dnsSocket.soTimeout = 2000
            try {
                dnsSocket.receive(respPacket)
            } catch (e: Exception) {
                dnsSocket.close()
                continue
            }
            dnsSocket.close()
            // Build response packet (reuse original IP/UDP headers, swap src/dst)
            // Swap IP src/dst
            val resp = buf.copyOfRange(0, dnsPayloadOffset) + respPacket.data.copyOfRange(0, respPacket.length)
            // Swap IP addresses
            for (i in 12..15) {
                val tmp = resp[i]
                resp[i] = resp[i + 4]
                resp[i + 4] = tmp
            }
            // Swap UDP ports
            val tmpPort1 = resp[20]
            val tmpPort2 = resp[21]
            resp[20] = resp[22]
            resp[21] = resp[23]
            resp[22] = tmpPort1
            resp[23] = tmpPort2
            // Set UDP length
            val respUdpLen = respPacket.length + 8
            resp[24] = ((respUdpLen shr 8) and 0xFF).toByte()
            resp[25] = (respUdpLen and 0xFF).toByte()
            // Set total length
            val respTotalLen = resp.size
            resp[2] = ((respTotalLen shr 8) and 0xFF).toByte()
            resp[3] = (respTotalLen and 0xFF).toByte()
            // Write response back to VPN interface
            try {
                output.write(resp)
            } catch (_: Exception) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TriplesVpnService", "onDestroy: VPN service is being destroyed and tunnel closed")
        vpnJob?.cancel()
        dnsForwardJob?.cancel()
        vpnInterface?.close()
        vpnInterface = null
        httpServer?.stop()
        httpServer = null
        dnsServer?.stopServer()
        dnsServer = null
        Log.i("TriplesVpnService", "VPN service stopped")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i("TriplesVpnService", "onTaskRemoved: stopping VPN service")
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }
} 