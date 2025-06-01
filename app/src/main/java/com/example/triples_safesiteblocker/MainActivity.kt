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

class MainActivity : AppCompatActivity() {
    private lateinit var websiteAdapter: WebsiteAdapter
    private val websites = mutableListOf<Website>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set app logo in header
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
                    websites.add(Website(host, true))
                    websiteAdapter.notifyItemInserted(websites.size - 1)
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

    data class Website(val url: String, val isBlocked: Boolean) {
        fun getFaviconUrl(): String {
            return "https://www.google.com/s2/favicons?sz=64&domain_url=https://$url"
        }
    }

    class WebsiteAdapter(private val websites: List<Website>) : RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_website, parent, false)
            return WebsiteViewHolder(view)
        }

        override fun onBindViewHolder(holder: WebsiteViewHolder, position: Int) {
            holder.bind(websites[position])
        }

        override fun getItemCount() = websites.size

        class WebsiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(website: Website) {
                val favicon = itemView.findViewById<ImageView>(R.id.imageWebsiteFavicon)
                val urlText = itemView.findViewById<TextView>(R.id.textWebsiteUrl)
                val statusText = itemView.findViewById<TextView>(R.id.textWebsiteStatus)
                val toggleButton = itemView.findViewById<ImageButton>(R.id.buttonToggleBlock)
                val deleteButton = itemView.findViewById<ImageButton>(R.id.buttonRemove)

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
                    // Show a mock interaction (e.g., Toast)
                }
                deleteButton.setOnClickListener {
                    // Show a mock interaction (e.g., Toast)
                }
            }
        }
    }
}