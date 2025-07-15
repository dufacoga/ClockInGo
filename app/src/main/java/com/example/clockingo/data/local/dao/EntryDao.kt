package com.example.clockingo.data.local.dao

import androidx.room.*
import com.example.clockingo.data.local.model.EntryEntity
import com.example.clockingo.data.local.model.LocationEntity

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EntryEntity)

    @Update
    suspend fun update(entry: EntryEntity)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM entries")
    suspend fun deleteAllEntries()

    @Query("SELECT * FROM entries")
    suspend fun getAllEntries(): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE isSynced = 0")
    suspend fun getUnsyncedEntries(): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getEntryById(id: Int): EntryEntity?

    @Query("SELECT * FROM entries WHERE userId = :userId ORDER BY entryTime DESC")
    suspend fun getEntriesByUser(userId: Int): List<EntryEntity>

    @Query("UPDATE entries SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}