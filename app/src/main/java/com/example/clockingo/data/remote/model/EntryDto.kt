package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class EntryDto(
    val id: Int,
    val userId: Int,
    val locationId: Int,
    val entryTime: String, // ISO 8601
    val selfie: String? = null, // En base64 si la API la devuelve así
    val updatedAt: String? = null,
    val isSynced: Boolean = false,
    val deviceId: String? = null
)