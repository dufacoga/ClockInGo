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
    val IsSynced: Int,
    val DeviceId: String? = null
)

//Code tested for MySQL
//@Serializable
//data class EntryDto(
//    val Id: Int,
//    val UserId: Int,
//    val LocationId: Int,
//    val EntryTime: String,
//    val Selfie: String? = null,
//    val UpdatedAt: String? = null,
//    val IsSynced: Boolean,
//    val DeviceId: String? = null
//)