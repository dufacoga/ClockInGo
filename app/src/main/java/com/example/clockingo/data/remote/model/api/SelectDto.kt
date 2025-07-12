package com.example.clockingo.data.remote.model.api

import com.google.gson.JsonElement
import kotlinx.serialization.Serializable

@Serializable
data class SelectDto(
    val table: String,
    val columns: List<String> = listOf("*"),
    val where: Map<String, JsonElement>? = null
)