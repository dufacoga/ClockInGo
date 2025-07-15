package com.example.clockingo.data.repository

import android.content.Context
import android.util.Log
import com.example.clockingo.data.local.ConnectivityObserver
import com.example.clockingo.data.local.dao.EntryDao
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.local.mapper.toEntity
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain as DtoToDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.data.work.scheduleEntrySync
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.repository.IEntryRepository
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response

class EntryRepository(
    private val context: Context,
    private val dao: EntryDao,
    private val connectivityObserver: ConnectivityObserver
) : IEntryRepository {
    private val api = RetrofitInstance.entryApi

    override suspend fun getAllEntries(): Response<List<Entry>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(table = "Entries")
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val entriesDto = apiResponse.body() ?: emptyList()
                    val entries = entriesDto.map { it.DtoToDomain() }
                    dao.deleteAllEntries()
                    entries.forEach { dao.insert(it.copy(isSynced = true).toEntity()) }
                    Response.success(entries)
                } else {
                    Log.w("EntryRepository", "API failed for getAllEntries (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getAllEntries().map { it.toDomain() })
                }
            } else {
                Log.i("EntryRepository", "No internet, loading getAllEntries from local DB.")
                Response.success(dao.getAllEntries().map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getAllEntries", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getEntryById(id: Int): Response<Entry?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Entries",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val entryDto = apiResponse.body()?.firstOrNull()
                    val entry = entryDto?.DtoToDomain()
                    entry?.let { dao.insert(it.copy(isSynced = true).toEntity()) } // Update specific item in cache, mark as synced
                    Response.success(entry)
                } else {
                    Log.w("EntryRepository", "API failed for getEntryById (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getEntryById(id)?.toDomain())
                }
            } else {
                Log.i("EntryRepository", "No internet, loading getEntryById from local DB.")
                Response.success(dao.getEntryById(id)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getEntryById", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getEntriesByUser(userId: Int): Response<List<Entry>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Entries",
                    where = mapOf("UserId" to JsonPrimitive(userId))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val entriesDto = apiResponse.body() ?: emptyList()
                    val entries = entriesDto.map { it.DtoToDomain() }
                    entries.forEach { dao.insert(it.copy(isSynced = true).toEntity()) }
                    Response.success(entries)
                } else {
                    Log.w("EntryRepository", "API failed for getEntriesByUser (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getEntriesByUser(userId).map { it.toDomain() })
                }
            } else {
                Log.i("EntryRepository", "No internet, loading getEntriesByUser from local DB.")
                Response.success(dao.getEntriesByUser(userId).map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in getEntriesByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun createEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = InsertDto(
                    table = "Entries",
                    values = mapOf(
                        "UserId" to JsonPrimitive(entry.userId),
                        "LocationId" to JsonPrimitive(entry.locationId),
                        "EntryTime" to JsonPrimitive(entry.entryTime),
                        "Selfie" to JsonPrimitive(entry.selfie ?: ""),
                        "UpdatedAt" to JsonPrimitive(entry.updatedAt ?: ""),
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(entry.deviceId)
                    )
                )
                val response = api.insert(dto)
                if (response.isSuccessful) {
                    dao.insert(entry.copy(isSynced = true).toEntity())
                } else {
                    dao.insert(entry.copy(isSynced = false).toEntity())
                    scheduleEntrySync(context)
                }
                response
            } else {
                dao.insert(entry.copy(isSynced = false).toEntity())
                scheduleEntrySync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            dao.insert(entry.copy(isSynced = false).toEntity())
            scheduleEntrySync(context)
            Log.e("EntryRepository", "Exception in createEntry", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun updateEntry(entry: Entry): Response<SqlQueryResponse<Unit>> {
        return try {
            dao.update(entry.copy(isSynced = false).toEntity())
            if (connectivityObserver.currentStatus()) {
                val dto = UpdateDto(
                    table = "Entries",
                    set = mapOf(
                        "UserId" to JsonPrimitive(entry.userId),
                        "LocationId" to JsonPrimitive(entry.locationId),
                        "EntryTime" to JsonPrimitive(entry.entryTime),
                        "Selfie" to JsonPrimitive(entry.selfie ?: ""),
                        "UpdatedAt" to JsonPrimitive(entry.updatedAt ?: ""),
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(entry.deviceId)
                    ),
                    where = mapOf("Id" to JsonPrimitive(entry.id))
                )
                val response = api.update(dto)
                if (response.isSuccessful) {
                    dao.update(entry.copy(isSynced = true).toEntity())
                } else {
                    scheduleEntrySync(context)
                }
                response
            } else {
                scheduleEntrySync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            dao.update(entry.copy(isSynced = false).toEntity())
            scheduleEntrySync(context)
            Log.e("EntryRepository", "Exception in updateEntry", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun deleteEntry(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            dao.deleteById(id)
            if (connectivityObserver.currentStatus()) {
                val dto = DeleteDto(
                    table = "Entries",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val response = api.delete(dto)
                if (!response.isSuccessful) {
                    Log.e("EntryRepository", "API deletion of Entry $id failed, consider re-adding to local as pending.")
                    scheduleEntrySync(context)
                }
                response
            } else {
                scheduleEntrySync(context)
                Response.success(SqlQueryResponse(Unit))
            }
        } catch (e: Exception) {
            Log.e("EntryRepository", "Exception in deleteEntry", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }
}