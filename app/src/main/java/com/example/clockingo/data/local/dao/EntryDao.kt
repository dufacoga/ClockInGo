package com.example.clockingo.data.local.dao

import androidx.room.*
import com.example.clockingo.data.local.model.EntryEntity

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EntryEntity)

    @Query("SELECT * FROM entries WHERE isSynced = 0")
    suspend fun getUnsyncedEntries(): List<EntryEntity>

    @Query("UPDATE entries SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("SELECT * FROM entries WHERE userId = :userId ORDER BY entryTime DESC")
    suspend fun getEntriesByUser(userId: Int): List<EntryEntity>
}