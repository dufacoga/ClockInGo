package com.example.clockingo.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val Id: Int,
    val Name: String,
    val Phone: String? = null,
    val Username: String,
    val AuthToken: String,
    val RoleId: Int
)