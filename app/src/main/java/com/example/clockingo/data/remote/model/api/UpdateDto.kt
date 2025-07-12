package com.example.clockingo.data.remote.model.api

import com.google.gson.JsonElement
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDto(
    val table: String,
    val values: Map<String, JsonElement>,
    val where: Map<String, JsonElement>
)