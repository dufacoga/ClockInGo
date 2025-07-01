package com.example.clockingo.domain.model

data class Entry(
    val id: Int,
    val userId: Int,
    val locationId: Int,
    val entryTime: String,
    val selfie: String? = null,
    val updatedAt: String? = null,
    val isSynced: Boolean = false,
    val deviceId: String? = null
)