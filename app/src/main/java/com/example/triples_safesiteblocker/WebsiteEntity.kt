package com.example.triples_safesiteblocker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "websites")
data class WebsiteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val isBlocked: Boolean
) 