package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    val Id: Int,
    val Name: String
)