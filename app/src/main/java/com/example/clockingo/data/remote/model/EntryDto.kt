package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class EntryDto(
    val Id: Int,
    val UserId: Int,
    val LocationId: Int,
    val EntryTime: String,
    val Selfie: String? = null,
    val UpdatedAt: String? = null,
    val IsSynced: Boolean = false,
    val DeviceId: String? = null
)