package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Exit
import com.example.clockingo.domain.repository.IExitRepository
import com.google.gson.JsonPrimitive
import retrofit2.Response

class ExitRepository : IExitRepository {
    private val api = RetrofitInstance.exitApi

    override suspend fun getAllExits(): Response<List<Exit>> {
        return try {
            val dto = SelectDto(table = "Exits")
            val response = api.select(dto)
            val exitsDto = response.body() ?: emptyList()
            val exits = exitsDto.map { it.toDomain() }
            return Response.success(exits)
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in getAllExits", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getExitById(id: Int): Response<Exit?> {
        return try {
            val dto = SelectDto(
                table = "Exits",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.select(dto)
            val exitDto = response.body()?.firstOrNull()
            return Response.success(exitDto?.toDomain())
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in getExitById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = InsertDto(
                table = "Exits",
                values = mapOf(
                    "UserId" to JsonPrimitive(exit.userId),
                    "LocationId" to JsonPrimitive(exit.locationId),
                    "ExitTime" to JsonPrimitive(exit.exitTime),
                    "EntryId" to JsonPrimitive(exit.entryId),
                    "Result" to JsonPrimitive(exit.result ?: ""),
                    "IrregularBehavior" to JsonPrimitive(exit.irregularBehavior),
                    "ReviewedByAdmin" to JsonPrimitive(exit.reviewedByAdmin),
                    "UpdatedAt" to JsonPrimitive(exit.updatedAt ?: ""),
                    "IsSynced" to JsonPrimitive(exit.isSynced),
                    "DeviceId" to JsonPrimitive(exit.deviceId)
                )
            )
            api.insert(dto)
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in createExit", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = UpdateDto(
                table = "Exits",
                values = mapOf(
                    "UserId" to JsonPrimitive(exit.userId),
                    "LocationId" to JsonPrimitive(exit.locationId),
                    "ExitTime" to JsonPrimitive(exit.exitTime),
                    "EntryId" to JsonPrimitive(exit.entryId),
                    "Result" to JsonPrimitive(exit.result ?: ""),
                    "IrregularBehavior" to JsonPrimitive(exit.irregularBehavior),
                    "ReviewedByAdmin" to JsonPrimitive(exit.reviewedByAdmin),
                    "UpdatedAt" to JsonPrimitive(exit.updatedAt ?: ""),
                    "IsSynced" to JsonPrimitive(exit.isSynced),
                    "DeviceId" to JsonPrimitive(exit.deviceId)
                ),
                where = mapOf("Id" to JsonPrimitive(exit.id))
            )
            api.update(dto)
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in updateExit", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteExit(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = DeleteDto(
                table = "Exits",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            api.delete(dto)
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in deleteExit", e)
            Response.error(500, null)
        }
    }
}