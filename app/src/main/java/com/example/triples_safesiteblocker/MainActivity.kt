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

class MainActivity : AppCompatActivity() {
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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerWebsites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Static data: 3 websites (2 blocked, 1 unblocked)
        val websites = listOf(
            Website("facebook.com", true),
            Website("twitter.com", false),
            Website("youtube.com", true)
        )
        recyclerView.adapter = WebsiteAdapter(websites)
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

                // Load real favicon from website URL
                Glide.with(itemView.context)
                    .load(website.getFaviconUrl())
                    .placeholder(R.drawable.bg_favicon)
                    .error(R.drawable.logo)
                    .into(favicon)
                // Set URL and status
                urlText.text = website.url
                statusText.text = if (website.isBlocked) "Blocked" else "Allowed"
                // Set lock/unlock icon (custom imported drawable)
                toggleButton.setImageResource(
                    if (website.isBlocked) R.drawable.locked else R.drawable.unlocked
                )
                // Set delete icon (custom imported drawable)
                deleteButton.setImageResource(R.drawable.trash)

                // Mock click listeners
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