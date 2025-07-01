package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ExitDto(
    val Id: Int,
    val UserId: Int,
    val LocationId: Int,
    val ExitTime: String,
    val EntryId: Int,
    val Result: String? = null,
    val IrregularBehavior: Boolean = false,
    val ReviewedByAdmin: Boolean = false,
    val UpdatedAt: String? = null,
    val IsSynced: Boolean = false,
    val DeviceId: String? = null
)