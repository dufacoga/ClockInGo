package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: Int,
    val code: String,
    val address: String? = null,
    val city: String? = null,
    val createdBy: Int,
    val isCompanyOffice: Boolean = false
)