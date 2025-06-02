package com.example.triples_safesiteblocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.example.triples_safesiteblocker.FaviconRequestListener
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.VpnService
import android.widget.Switch

class MainActivity : AppCompatActivity() {
    private lateinit var websiteAdapter: WebsiteAdapter
    private lateinit var websiteDatabase: WebsiteDatabase
    private lateinit var websiteDao: WebsiteDao
    private val websites = mutableListOf<Website>()
    private var isVpnRunning = false
    private lateinit var vpnSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        websiteDatabase = WebsiteDatabase.getDatabase(this)
        websiteDao = websiteDatabase.websiteDao()

        val logoView = findViewById<ImageView>(R.id.imageLogo)
        logoView.setImageResource(R.drawable.logo)

        val addButton = findViewById<ImageButton>(R.id.buttonAdd)
        addButton.setImageResource(R.drawable.add_website)
        addButton.setOnClickListener {
            showAddWebsiteDialog()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerWebsites)
        recyclerView.layoutManager = LinearLayoutManager(this)
        websiteAdapter = WebsiteAdapter(websites)
        recyclerView.adapter = websiteAdapter

        // Load websites from Room
        lifecycleScope.launch {
            val dbWebsites = websiteDao.getAll()
            websites.clear()
            websites.addAll(dbWebsites.map { Website(it.id, it.url, it.isBlocked) })
            websiteAdapter.notifyDataSetChanged()
        }

        vpnSwitch = findViewById(R.id.switchVpn)
        vpnSwitch.isChecked = false
        val vpnListener = { _: android.widget.CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Log.i("MainActivity", "VPN toggle ON by user")
                val intent = VpnService.prepare(this@MainActivity)
                if (intent != null) {
                    startActivityForResult(intent, 100)
                } else {
                    startVpnService()
                }
            } else {
                Log.i("MainActivity", "VPN toggle OFF by user")
                stopVpnService()
            }
        }
        vpnSwitch.setOnCheckedChangeListener(vpnListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            startVpnService()
            vpnSwitch.setOnCheckedChangeListener(null)
            vpnSwitch.isChecked = true
            val vpnListener = { _: android.widget.CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    Log.i("MainActivity", "VPN toggle ON by user")
                    val intent = VpnService.prepare(this@MainActivity)
                    if (intent != null) {
                        startActivityForResult(intent, 100)
                    } else {
                        startVpnService()
                    }
                } else {
                    Log.i("MainActivity", "VPN toggle OFF by user")
                    stopVpnService()
                }
            }
            vpnSwitch.setOnCheckedChangeListener(vpnListener)
        }
    }

    private fun startVpnService() {
        Log.i("MainActivity", "Starting VPN service")
        val intent = Intent(this, TriplesVpnService::class.java)
        startService(intent)
        isVpnRunning = true
    }

    private fun stopVpnService() {
        Log.i("MainActivity", "Stopping VPN service")
        val intent = Intent(this, TriplesVpnService::class.java)
        intent.action = "STOP_VPN"
        startService(intent)
        isVpnRunning = false
    }

    private fun showAddWebsiteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_website, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialogView.findViewById<View>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.buttonAdd).setOnClickListener {
            val editText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextWebsite)
            val input = editText.text?.toString()?.trim() ?: ""
            val host = extractHost(input)
            if (host == null) {
                val inputLayout = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayout)
                inputLayout.error = "Please enter a valid website URL or host."
            } else {
                dialog.dismiss()
                // Check for duplicates
                if (websites.any { it.url.equals(host, ignoreCase = true) }) {
                    android.widget.Toast.makeText(this, "This website is already in the list.", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        websiteDao.insert(WebsiteEntity(url = host, isBlocked = true))
                        val dbWebsites = websiteDao.getAll()
                        websites.clear()
                        websites.addAll(dbWebsites.map { Website(it.id, it.url, it.isBlocked) })
                        websiteAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun extractHost(input: String): String? {
        // Try to parse as URL, fallback to host
        return try {
            val url = if (input.startsWith("http://") || input.startsWith("https://")) input else "https://$input"
            val uri = android.net.Uri.parse(url)
            val host = uri.host
            if (host.isNullOrBlank() || !host.contains('.')) null else host.removePrefix("www.")
        } catch (e: Exception) {
            null
        }
    }

    data class Website(val id: Int, val url: String, val isBlocked: Boolean) {
        fun getFaviconUrl(): String {
            return "https://www.google.com/s2/favicons?sz=64&domain_url=https://$url"
        }
    }

    class WebsiteAdapter(private val websites: MutableList<Website>) : RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_website, parent, false)
            return WebsiteViewHolder(view)
        }

        override fun onBindViewHolder(holder: WebsiteViewHolder, position: Int) {
            holder.bind(websites[position])
        }

        override fun getItemCount() = websites.size

        inner class WebsiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val favicon = itemView.findViewById<ImageView>(R.id.imageWebsiteFavicon)
            private val urlText = itemView.findViewById<TextView>(R.id.textWebsiteUrl)
            private val statusText = itemView.findViewById<TextView>(R.id.textWebsiteStatus)
            private val toggleButton = itemView.findViewById<ImageButton>(R.id.buttonToggleBlock)
            private val deleteButton = itemView.findViewById<ImageButton>(R.id.buttonRemove)

            fun getFaviconUrl(host: String) =
                "https://www.google.com/s2/favicons?sz=64&domain_url=https://$host"

            fun loadFaviconWithFallback(host: String, fallbackHost: String?) {
                Glide.with(itemView.context)
                    .load(getFaviconUrl(host))
                    .placeholder(R.drawable.bg_favicon)
                    .error(R.drawable.bg_favicon)
                    .listener(FaviconRequestListener(fallbackHost, favicon, itemView.context))
                    .into(favicon)
            }

            fun bind(website: Website) {
                val host = website.url
                val hasWww = host.startsWith("www.")
                val fallbackHost = if (hasWww) host.removePrefix("www.") else "www.$host"
                loadFaviconWithFallback(host, fallbackHost)

                urlText.text = website.url
                statusText.text = if (website.isBlocked) "Blocked" else "Allowed"
                toggleButton.setImageResource(
                    if (website.isBlocked) R.drawable.locked else R.drawable.unlocked
                )
                deleteButton.setImageResource(R.drawable.trash)

                toggleButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val websiteToToggle = websites[position]
                        val newBlockedState = !websiteToToggle.isBlocked
                        (itemView.context as? MainActivity)?.let { activity ->
                            activity.lifecycleScope.launch {
                                activity.websiteDao.update(
                                    WebsiteEntity(
                                        id = websiteToToggle.id,
                                        url = websiteToToggle.url,
                                        isBlocked = newBlockedState
                                    )
                                )
                                val dbWebsites = activity.websiteDao.getAll()
                                websites.clear()
                                websites.addAll(dbWebsites.map { Website(it.id, it.url, it.isBlocked) })
                                notifyDataSetChanged()
                                val toastMsg = if (newBlockedState) {
                                    "${websiteToToggle.url} is now blocked"
                                } else {
                                    "${websiteToToggle.url} is unlocked"
                                }
                                val toast = android.widget.Toast.makeText(activity, toastMsg, android.widget.Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    }
                }
                deleteButton.setOnClickListener {
                    androidx.appcompat.app.AlertDialog.Builder(itemView.context)
                        .setTitle("Remove Website")
                        .setMessage("Are you sure you want to remove this website from the list?")
                        .setPositiveButton("Delete") { _, _ ->
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val websiteToDelete = websites[position]
                                (itemView.context as? MainActivity)?.let { activity ->
                                    activity.lifecycleScope.launch {
                                        activity.websiteDao.delete(WebsiteEntity(id = websiteToDelete.id, url = websiteToDelete.url, isBlocked = websiteToDelete.isBlocked))
                                        val dbWebsites = activity.websiteDao.getAll()
                                        websites.clear()
                                        websites.addAll(dbWebsites.map { Website(it.id, it.url, it.isBlocked) })
                                        notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }
}