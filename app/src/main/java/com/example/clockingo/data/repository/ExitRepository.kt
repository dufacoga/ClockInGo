package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryRequest
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.domain.model.Exit
import com.example.clockingo.domain.repository.IExitRepository
import retrofit2.Response

class ExitRepository : IExitRepository {
    private val api = RetrofitInstance.exitApi

    override suspend fun getAllExits(): Response<List<Exit>> {
        return try {
            val query = "SELECT * FROM Exits"
            val response = api.executeQuery(SqlQueryRequest(query))
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
            val query = "SELECT * FROM Exits WHERE Id = $id"
            val response = api.executeQuery(SqlQueryRequest(query))
            val exitDto = response.body()?.firstOrNull()
            return Response.success(exitDto?.toDomain())
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in getExitById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            val resultValue = exit.result?.let { "'$it'" } ?: "NULL"
            val updatedAtValue = exit.updatedAt?.let { "'$it'" } ?: "CURRENT_TIMESTAMP()"

            val query = """
            INSERT INTO Exits (UserId, LocationId, ExitTime, EntryId, Result, IrregularBehavior, ReviewedByAdmin, UpdatedAt, IsSynced, DeviceId) 
            VALUES (${exit.userId}, ${exit.locationId}, '${exit.exitTime}', ${exit.entryId}, $resultValue, ${exit.irregularBehavior}, ${exit.reviewedByAdmin}, $updatedAtValue, ${exit.isSynced}, '${exit.deviceId}')
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in createExit", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            val resultValue = exit.result?.let { "Result = '$it'" } ?: "Result = NULL"
            val updatedAtValue = exit.updatedAt?.let { "UpdatedAt = '$it'" } ?: "UpdatedAt = CURRENT_TIMESTAMP()"

            val query = """
            UPDATE Exits SET 
            UserId = ${exit.userId}, 
            LocationId = ${exit.locationId},
            ExitTime = '${exit.exitTime}',
            EntryId = ${exit.entryId},
            $resultValue, 
            IrregularBehavior = ${exit.irregularBehavior},
            ReviewedByAdmin = ${exit.reviewedByAdmin}, 
            $updatedAtValue, 
            IsSynced = ${exit.isSynced},
            DeviceId = '${exit.deviceId}'
            WHERE Id = ${exit.id}
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in updateExit", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteExit(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = "DELETE FROM Exits WHERE Id = $id"
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in deleteExit", e)
            Response.error(500, null)
        }
    }
}