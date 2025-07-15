package com.example.clockingo.data.repository

import android.content.Context
import android.util.Log
import com.example.clockingo.data.local.ConnectivityObserver
import com.example.clockingo.data.local.dao.LocationDao
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.local.mapper.toEntity
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain as DtoToDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.repository.ILocationRepository
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response

class LocationRepository(
    private val dao: LocationDao,
    private val connectivityObserver: ConnectivityObserver
) : ILocationRepository {
    private val api = RetrofitInstance.locationApi

    override suspend fun getAllLocations(): Response<List<Location>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(table = "Locations")
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val locationsDto = apiResponse.body() ?: emptyList()
                    val locations = locationsDto.map { it.DtoToDomain() }
                    dao.deleteAllLocations()
                    locations.forEach { dao.insert(it.toEntity()) }
                    Response.success(locations)
                } else {
                    Log.w("LocationRepository", "API failed for getAllLocations (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getAllLocations().map { it.toDomain() })
                }
            } else {
                Log.w("LocationRepository", "No internet connection, loading from local DB.")
                Response.success(dao.getAllLocations().map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getAllLocations", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getLocationById(id: Int): Response<Location?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Locations",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val locationDto = apiResponse.body()?.firstOrNull()
                    val location = locationDto?.DtoToDomain()
                    location?.let { dao.insert(it.toEntity()) }
                    Response.success(location)
                } else {
                    Log.w("LocationRepository", "API failed for getLocationById (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getLocationById(id)?.toDomain())
                }
            } else {
                Log.w("LocationRepository", "No internet connection, loading from local DB.")
                Response.success(dao.getLocationById(id)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getLocationById", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getLocationByCode(code: String): Response<Location?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Locations",
                    where = mapOf("Code" to JsonPrimitive(code))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val locationDto = apiResponse.body()?.firstOrNull()
                    val location = locationDto?.DtoToDomain()
                    location?.let { dao.insert(it.toEntity()) }
                    Response.success(location)
                } else {
                    Log.w("LocationRepository", "API failed for getLocationByCode (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getLocationByCode(code)?.toDomain())
                }
            } else {
                Log.i("LocationRepository", "No internet, loading getLocationByCode from local DB.")
                Response.success(dao.getLocationByCode(code)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getLocationByCode", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun createLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = InsertDto(
                table = "Locations",
                values = mapOf(
                    "Code" to JsonPrimitive(location.code),
                    "Address" to JsonPrimitive(location.address ?: ""),
                    "City" to JsonPrimitive(location.city ?: ""),
                    "CreatedBy" to JsonPrimitive(location.createdBy),
                    "IsCompanyOffice" to JsonPrimitive(location.isCompanyOffice)
                )
            )
            val response = api.insert(dto)
            if (response.isSuccessful) {
                dao.insert(location.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in createLocation", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun updateLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = UpdateDto(
                table = "Locations",
                set = mapOf(
                    "Code" to JsonPrimitive(location.code),
                    "Address" to JsonPrimitive(location.address ?: ""),
                    "City" to JsonPrimitive(location.city ?: ""),
                    "CreatedBy" to JsonPrimitive(location.createdBy),
                    "IsCompanyOffice" to JsonPrimitive(location.isCompanyOffice)
                ),
                where = mapOf("Id" to JsonPrimitive(location.id))
            )
            val response = api.update(dto)
            if (response.isSuccessful) {
                dao.insert(location.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in updateLocation", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun deleteLocation(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = DeleteDto(
                table = "Locations",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.delete(dto)
            if (response.isSuccessful) {
                val locationEntity = dao.getLocationById(id)
                locationEntity?.let { dao.delete(it) }
            }
            response
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in deleteLocation", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }
}