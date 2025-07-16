package com.example.clockingo.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val address: String?,
    val city: String?,
    val createdBy: Int,
    val isCompanyOffice: Boolean = false
)