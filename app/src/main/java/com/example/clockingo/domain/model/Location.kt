package com.example.clockingo.domain.model

data class Location(
    val id: Int,
    val code: String,
    val address: String? = null,
    val city: String? = null,
    val createdBy: Int,
    val isCompanyOffice: Boolean = false
)