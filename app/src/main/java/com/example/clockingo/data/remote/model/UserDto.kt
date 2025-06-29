package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val phone: String? = null,
    val username: String,
    val authToken: String,
    val roleId: Int
)