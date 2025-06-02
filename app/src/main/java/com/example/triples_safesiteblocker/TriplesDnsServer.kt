package com.example.triples_safesiteblocker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class TriplesDnsServer(private val blockedHosts: Set<String>) : Thread() {
    private var running = true
    private val listenPort = 53535
    private val forwardDns = "8.8.8.8"
    private val forwardPort = 53

    override fun run() {
        DatagramSocket(listenPort).use { socket ->
            val buf = ByteArray(512)
            while (running) {
                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)
                val query = packet.data.copyOf(packet.length)
                val host = extractHostFromDnsQuery(query)
                if (host != null && blockedHosts.contains(host)) {
                    // Respond with 127.0.0.1
                    val response = createBlockedDnsResponse(query, packet.address, packet.port)
                    socket.send(response)
                } else if (host != null) {
                    // Use Java's DNS resolution for allowed domains
                    try {
                        val resolved = InetAddress.getByName(host)
                        val response = createAllowedDnsResponse(query, packet.address, packet.port, resolved)
                        socket.send(response)
                    } catch (e: Exception) {
                        // If resolution fails, just drop the packet (or could send NXDOMAIN)
                    }
                } else {
                    // Forward to real DNS (fallback, rarely used)
                    val forwardSocket = DatagramSocket()
                    val forwardPacket = DatagramPacket(query, query.size, InetAddress.getByName(forwardDns), forwardPort)
                    forwardSocket.send(forwardPacket)
                    val response = DatagramPacket(ByteArray(512), 512)
                    forwardSocket.receive(response)
                    val respPacket = DatagramPacket(response.data, response.length, packet.address, packet.port)
                    socket.send(respPacket)
                    forwardSocket.close()
                }
            }
        }
    }

    fun stopServer() {
        running = false
    }

    // Extract the queried host from a DNS query packet (very basic, only works for simple queries)
    private fun extractHostFromDnsQuery(query: ByteArray): String? {
        try {
            var pos = 12 // DNS header is 12 bytes
            val labels = mutableListOf<String>()
            while (true) {
                val len = query[pos].toInt() and 0xFF
                if (len == 0) break
                val label = String(query, pos + 1, len)
                labels.add(label)
                pos += len + 1
            }
            return labels.joinToString(".")
        } catch (e: Exception) {
            return null
        }
    }

    // Create a DNS response that points to 127.0.0.1 (for A record queries)
    private fun createBlockedDnsResponse(query: ByteArray, address: InetAddress, port: Int): DatagramPacket {
        val response = query.copyOf()
        response[2] = (response[2].toInt() or 0x80).toByte() // Set response flag
        response[7] = 1 // 1 answer
        // Answer section: pointer to query, type A, class IN, TTL, RDLENGTH, RDATA (127.0.0.1)
        val answer = byteArrayOf(
            0xC0.toByte(), 0x0C, // pointer to domain name
            0x00, 0x01, // type A
            0x00, 0x01, // class IN
            0x00, 0x00, 0x00, 0x3C, // TTL 60
            0x00, 0x04, // RDLENGTH 4
            127, 0, 0, 1 // RDATA 127.0.0.1
        )
        val resp = response + answer
        return DatagramPacket(resp, resp.size, address, port)
    }

    // Create a DNS response that points to the resolved IP for allowed domains
    private fun createAllowedDnsResponse(query: ByteArray, address: InetAddress, port: Int, resolved: InetAddress): DatagramPacket {
        val response = query.copyOf()
        response[2] = (response[2].toInt() or 0x80).toByte() // Set response flag
        response[7] = 1 // 1 answer
        // Answer section: pointer to query, type A, class IN, TTL, RDLENGTH, RDATA (resolved IP)
        val ipBytes = resolved.address
        val answer = byteArrayOf(
            0xC0.toByte(), 0x0C, // pointer to domain name
            0x00, 0x01, // type A
            0x00, 0x01, // class IN
            0x00, 0x00, 0x00, 0x3C, // TTL 60
            0x00, 0x04, // RDLENGTH 4
            ipBytes[0], ipBytes[1], ipBytes[2], ipBytes[3] // RDATA
        )
        val resp = response + answer
        return DatagramPacket(resp, resp.size, address, port)
    }
} 