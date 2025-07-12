package com.example.clockingo.data.remote.model.api

import com.google.gson.JsonElement
import kotlinx.serialization.Serializable

@Serializable
data class DeleteDto(
    val table: String,
    val where: Map<String, JsonElement>
)