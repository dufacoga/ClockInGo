package com.example.clockingo.domain.model

data class Exit(
    val id: Int,
    val userId: Int,
    val locationId: Int,
    val exitTime: String,
    val entryId: Int,
    val result: String? = null,
    val irregularBehavior: Boolean = false,
    val reviewedByAdmin: Boolean = false,
    val updatedAt: String? = null,
    val isSynced: Boolean = false,
    val deviceId: String? = null
)