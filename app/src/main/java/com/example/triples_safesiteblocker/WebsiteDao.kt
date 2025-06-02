package com.example.triples_safesiteblocker

import androidx.room.*

@Dao
interface WebsiteDao {
    @Query("SELECT * FROM websites")
    suspend fun getAll(): List<WebsiteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(website: WebsiteEntity)

    @Delete
    suspend fun delete(website: WebsiteEntity)

    @Update
    suspend fun update(website: WebsiteEntity)
} 