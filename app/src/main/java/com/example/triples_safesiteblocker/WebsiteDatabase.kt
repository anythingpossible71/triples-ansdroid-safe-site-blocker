package com.example.triples_safesiteblocker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WebsiteEntity::class], version = 1)
abstract class WebsiteDatabase : RoomDatabase() {
    abstract fun websiteDao(): WebsiteDao

    companion object {
        @Volatile
        private var INSTANCE: WebsiteDatabase? = null

        fun getDatabase(context: Context): WebsiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WebsiteDatabase::class.java,
                    "website_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 