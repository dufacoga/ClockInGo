package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.repository.ILocationRepository
import com.google.gson.JsonPrimitive
import retrofit2.Response

class LocationRepository : ILocationRepository {
    private val api = RetrofitInstance.locationApi

    override suspend fun getAllLocations(): Response<List<Location>> {
        return try {
            val dto = SelectDto(table = "Locations")
            val response = api.select(dto)
            val locationsDto = response.body() ?: emptyList()
            val locations = locationsDto.map { it.toDomain() }
            return Response.success(locations)
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getAllLocations", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getLocationById(id: Int): Response<Location?> {
        return try {
            val dto = SelectDto(
                table = "Locations",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.select(dto)
            val locationDto = response.body()?.firstOrNull()
            return Response.success(locationDto?.toDomain())
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getLocationById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
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
            api.insert(dto)
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in createLocation", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = UpdateDto(
                table = "Locations",
                values = mapOf(
                    "Code" to JsonPrimitive(location.code),
                    "Address" to JsonPrimitive(location.address ?: ""),
                    "City" to JsonPrimitive(location.city ?: ""),
                    "CreatedBy" to JsonPrimitive(location.createdBy),
                    "IsCompanyOffice" to JsonPrimitive(location.isCompanyOffice)
                ),
                where = mapOf("Id" to JsonPrimitive(location.id))
            )
            api.update(dto)
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in updateLocation", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteLocation(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = DeleteDto(
                table = "Locations",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            api.delete(dto)
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in deleteLocation", e)
            Response.error(500, null)
        }
    }
}