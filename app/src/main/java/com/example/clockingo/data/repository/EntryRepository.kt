package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.repository.IEntryRepository
import com.google.gson.JsonPrimitive
import retrofit2.Response

class EntryRepository : IEntryRepository {
    private val api = RetrofitInstance.entryApi

    override suspend fun getAllEntries(): Response<List<Entry>> {
        return try {
            val dto = SelectDto(table = "Entries")
            val response = api.select(dto)
            val entriesDto = response.body() ?: emptyList()
            val entries = entriesDto.map { it.toDomain() }
            return Response.success(entries)
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getAllEntries", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getEntryById(id: Int): Response<Entry?> {
        return try {
            val dto = SelectDto(
                table = "Entries",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.select(dto)
            val entryDto = response.body()?.firstOrNull()
            return Response.success(entryDto?.toDomain())
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getEntryById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getEntriesByUser(userId: Int): Response<List<Entry>> {
        return try {
            val dto = SelectDto(
                table = "Entries",
                where = mapOf("UserId" to JsonPrimitive(userId))
            )
            val response = api.select(dto)
            val entriesDto = response.body() ?: emptyList()
            val entries = entriesDto.map { it.toDomain() }
            Response.success(entries)
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getEntriesByUser", e)
            Response.error(500, null)
        }
    }

    override suspend fun createEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = InsertDto(
                table = "Entries",
                values = mapOf(
                    "UserId" to JsonPrimitive(entry.userId),
                    "LocationId" to JsonPrimitive(entry.locationId),
                    "EntryTime" to JsonPrimitive(entry.entryTime),
                    "Selfie" to JsonPrimitive(entry.selfie ?: ""),
                    "UpdatedAt" to JsonPrimitive(entry.updatedAt ?: ""),
                    "IsSynced" to JsonPrimitive(entry.isSynced),
                    "DeviceId" to JsonPrimitive(entry.deviceId)
                )
            )
            api.insert(dto)
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in createEntry", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = UpdateDto(
                table = "Entries",
                set = mapOf(
                    "UserId" to JsonPrimitive(entry.userId),
                    "LocationId" to JsonPrimitive(entry.locationId),
                    "EntryTime" to JsonPrimitive(entry.entryTime),
                    "Selfie" to JsonPrimitive(entry.selfie ?: ""),
                    "UpdatedAt" to JsonPrimitive(entry.updatedAt ?: ""),
                    "IsSynced" to JsonPrimitive(entry.isSynced),
                    "DeviceId" to JsonPrimitive(entry.deviceId)
                ),
                where = mapOf("Id" to JsonPrimitive(entry.id))
            )
            api.update(dto)
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in updateEntry", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteEntry(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = DeleteDto(
                table = "Entries",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            api.delete(dto)
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in deleteEntry", e)
            Response.error(500, null)
        }
    }
}