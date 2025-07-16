package com.example.clockingo.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity( tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val locationId: Int,
    val entryTime: String,
    val selfie: String?,
    val updatedAt: String?,
    val isSynced: Boolean = false,
    val deviceId: String?
)