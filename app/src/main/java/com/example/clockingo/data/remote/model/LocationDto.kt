package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val Id: Int,
    val Code: String,
    val Address: String? = null,
    val City: String? = null,
    val CreatedBy: Int,
    val IsCompanyOffice: Boolean = false
)