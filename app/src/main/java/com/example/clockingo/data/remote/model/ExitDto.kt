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
    val IrregularBehavior: Int,
    val ReviewedByAdmin: Int,
    val UpdatedAt: String? = null,
    val IsSynced: Int,
    val DeviceId: String? = null
)

//Code tested for MySQL
//@Serializable
//data class ExitDto(
//    val Id: Int,
//    val UserId: Int,
//    val LocationId: Int,
//    val ExitTime: String,
//    val EntryId: Int,
//    val Result: String? = null,
//    val IrregularBehavior: Boolean,
//    val ReviewedByAdmin: Boolean,
//    val UpdatedAt: String? = null,
//    val IsSynced: Boolean,
//    val DeviceId: String? = null
//)