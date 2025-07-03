package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryRequest
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.repository.IEntryRepository
import retrofit2.Response

class EntryRepository : IEntryRepository {
    private val api = RetrofitInstance.entryApi

    override suspend fun getAllEntries(): Response<List<Entry>> {
        return try {
            val query = "SELECT * FROM Entries"
            val response = api.executeQuery(SqlQueryRequest(query))
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
            val query = "SELECT * FROM Entries WHERE Id = $id"
            val response = api.executeQuery(SqlQueryRequest(query))
            val entryDto = response.body()?.firstOrNull()
            return Response.success(entryDto?.toDomain())
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getEntryById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            val selfieValue = entry.selfie?.let { "'$it'" } ?: "NULL"
            val updatedAtValue = entry.updatedAt?.let { "'$it'" } ?: "CURRENT_TIMESTAMP()"

            val query = """
            INSERT INTO Entries (UserId, LocationId, EntryTime, Selfie, UpdatedAt, IsSynced, DeviceId) 
            VALUES (${entry.userId}, ${entry.locationId}, '${entry.entryTime}', $selfieValue, $updatedAtValue, ${entry.isSynced}, '${entry.deviceId}')
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in createEntry", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            val selfieValue = entry.selfie?.let { "Selfie = '$it'" } ?: "Selfie = NULL"
            val updatedAtValue = entry.updatedAt?.let { "UpdatedAt = '$it'" } ?: "UpdatedAt = CURRENT_TIMESTAMP()"

            val query = """
            UPDATE Entries SET 
            UserId = ${entry.userId}, 
            LocationId = ${entry.locationId},
            EntryTime = '${entry.entryTime}',
            $selfieValue,
            $updatedAtValue, 
            IsSynced = ${entry.isSynced},
            DeviceId = '${entry.deviceId}'
            WHERE Id = ${entry.id}
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in updateEntry", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteEntry(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = "DELETE FROM Entries WHERE Id = $id"
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in deleteEntry", e)
            Response.error(500, null)
        }
    }
}