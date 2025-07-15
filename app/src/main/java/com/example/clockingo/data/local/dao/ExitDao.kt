package com.example.clockingo.data.local.dao

import androidx.room.*
import com.example.clockingo.data.local.model.ExitEntity

@Dao
interface ExitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exit: ExitEntity)

    @Update
    suspend fun update(exit: ExitEntity)

    @Query("DELETE FROM exits WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM exits")
    suspend fun deleteAllExits()

    @Query("SELECT * FROM exits")
    suspend fun getAllExits(): List<ExitEntity>

    @Query("SELECT * FROM exits WHERE id = :id")
    suspend fun getExitById(id: Int): ExitEntity?

    @Query("SELECT * FROM exits WHERE entryId = :entryId LIMIT 1")
    suspend fun getExitByEntryId(entryId: Int): ExitEntity?

    @Query("SELECT * FROM exits WHERE userId = :userId ORDER BY exitTime DESC")
    suspend fun getExitsByUser(userId: Int): List<ExitEntity>

    @Query("SELECT * FROM exits WHERE isSynced = 0")
    suspend fun getUnsyncedExits(): List<ExitEntity>

    @Query("UPDATE exits SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}