package com.example.clockingo.domain.model

data class User(
    val id: Int,
    val name: String,
    val phone: String? = null,
    val username: String,
    val authToken: String,
    val roleId: Int
)