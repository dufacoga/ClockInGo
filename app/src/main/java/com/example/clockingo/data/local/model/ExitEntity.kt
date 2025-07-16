package com.example.clockingo.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exits")
data class ExitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val locationId: Int,
    val exitTime: String,
    val entryId: Int,
    val result: String?,
    val irregularBehavior: Boolean = false,
    val reviewedByAdmin: Boolean = false,
    val updatedAt: String?,
    val isSynced: Boolean = false,
    val deviceId: String?
)