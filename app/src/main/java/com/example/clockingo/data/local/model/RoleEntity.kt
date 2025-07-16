package com.example.clockingo.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)