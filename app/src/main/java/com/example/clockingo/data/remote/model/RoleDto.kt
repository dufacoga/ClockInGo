package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    val id: Int,
    val name: String
)