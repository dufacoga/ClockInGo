package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ExitDto(
    val id: Int,
    val userId: Int,
    val locationId: Int,
    val exitTime: String, // ISO 8601
    val entryId: Int,
    val result: String? = null,
    val irregularBehavior: Boolean = false,
    val reviewedByAdmin: Boolean = false,
    val updatedAt: String? = null,
    val isSynced: Boolean = false,
    val deviceId: String? = null
)