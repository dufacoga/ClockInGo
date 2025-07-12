package com.example.clockingo.data.remote.model.api

import com.google.gson.JsonElement
import kotlinx.serialization.Serializable

@Serializable
data class InsertDto(
    val table: String,
    val values: Map<String, JsonElement>
)