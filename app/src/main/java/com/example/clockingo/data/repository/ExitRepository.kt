package com.example.clockingo.data.repository

import android.content.Context
import android.util.Log
import com.example.clockingo.data.local.ConnectivityObserver
import com.example.clockingo.data.local.dao.EntryDao
import com.example.clockingo.data.local.dao.ExitDao
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.local.mapper.toEntity
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain as DtoToDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.data.work.scheduleExitSync
import com.example.clockingo.domain.model.Exit
import com.example.clockingo.domain.repository.IExitRepository
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response

class ExitRepository(
    private val context: Context,
    private val dao: ExitDao,
    private val connectivityObserver: ConnectivityObserver
) : IExitRepository {
    private val api = RetrofitInstance.exitApi

    override suspend fun getAllExits(): Response<List<Exit>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(table = "Exits")
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val remoteExits = apiResponse.body()?.map { it.DtoToDomain() } ?: emptyList()
                    val localExits = dao.getAllExits().map { it.toDomain() }
                    val localExitMap = localExits.associateBy { it.id }
                    for (remoteExit in remoteExits) {
                        val existingLocal = localExitMap[remoteExit.id]
                        if (existingLocal != null) {
                            dao.insert(remoteExit.copy(isSynced = true).toEntity())
                        } else {
                            dao.insert(remoteExit.copy(isSynced = true).toEntity())
                        }
                    }
                    Response.success(dao.getAllExits().map { it.toDomain() })

                } else {
                    Log.w("ExitRepository", "API failed for getAllExits (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getAllExits().map { it.toDomain() })
                }
            } else {
                Log.i("ExitRepository", "No internet, loading getAllExits from local DB.")
                Response.success(dao.getAllExits().map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in getAllExits", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getExitById(id: Int): Response<Exit?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Exits",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val exitDto = apiResponse.body()?.firstOrNull()
                    val exit = exitDto?.DtoToDomain()
                    exit?.let { dao.insert(it.copy(isSynced = true).toEntity()) }
                    Response.success(exit)
                } else {
                    Log.w("ExitRepository", "API failed for getExitById (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getExitById(id)?.toDomain())
                }
            } else {
                Log.i("ExitRepository", "No internet, loading getExitById from local DB.")
                Response.success(dao.getExitById(id)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in getExitById", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun createExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            if (connectivityObserver.currentStatus()) {
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
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(exit.deviceId)
                    )
                )
                val response = api.insert(dto)
                if (response.isSuccessful) {
                    dao.insert(exit.copy(isSynced = true).toEntity())
                } else {
                    dao.insert(exit.copy(isSynced = false).toEntity())
                    scheduleExitSync(context)
                }
                response
            } else {
                dao.insert(exit.copy(isSynced = false).toEntity())
                scheduleExitSync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            dao.insert(exit.copy(isSynced = false).toEntity())
            scheduleExitSync(context)
            Log.e("ExitRepository", "Exception in createExit", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun updateExit(exit: Exit): Response<SqlQueryResponse<Unit>> {
        return try {
            dao.update(exit.copy(isSynced = false).toEntity())
            if (connectivityObserver.currentStatus()) {
                val dto = UpdateDto(
                    table = "Exits",
                    set = mapOf(
                        "UserId" to JsonPrimitive(exit.userId),
                        "LocationId" to JsonPrimitive(exit.locationId),
                        "ExitTime" to JsonPrimitive(exit.exitTime),
                        "EntryId" to JsonPrimitive(exit.entryId),
                        "Result" to JsonPrimitive(exit.result ?: ""),
                        "IrregularBehavior" to JsonPrimitive(exit.irregularBehavior),
                        "ReviewedByAdmin" to JsonPrimitive(exit.reviewedByAdmin),
                        "UpdatedAt" to JsonPrimitive(exit.updatedAt ?: ""),
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(exit.deviceId)
                    ),
                    where = mapOf("Id" to JsonPrimitive(exit.id))
                )
                val response = api.update(dto)
                if (response.isSuccessful) {
                    dao.update(exit.copy(isSynced = true).toEntity())
                } else {
                    scheduleExitSync(context)
                }
                response
            } else {
                scheduleExitSync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            dao.update(exit.copy(isSynced = false).toEntity())
            scheduleExitSync(context)
            Log.e("ExitRepository", "Exception in updateExit", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun deleteExit(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            dao.deleteById(id)
            if (connectivityObserver.currentStatus()) {
                val dto = DeleteDto(
                    table = "Exits",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val response = api.delete(dto)
                if (!response.isSuccessful) {
                    Log.e("ExitRepository", "API deletion of Exit $id failed, consider re-adding to local as pending.")
                    scheduleExitSync(context)
                }
                response
            } else {
                scheduleExitSync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            Log.e("ExitRepository", "Exception in deleteExit", e)
            scheduleExitSync(context)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }
}