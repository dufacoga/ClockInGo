package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryRequest
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.repository.ILocationRepository
import retrofit2.Response

class LocationRepository : ILocationRepository {
    private val api = RetrofitInstance.locationApi

    override suspend fun getAllLocations(): Response<List<Location>> {
        return try {
            val query = "SELECT * FROM Locations"
            val response = api.executeSelect(query)
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
            val query = "SELECT * FROM Locations WHERE Id = $id"
            val response = api.executeSelect(query)
            val locationDto = response.body()?.firstOrNull()
            return Response.success(locationDto?.toDomain())
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in getLocationById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
            val addressValue = location.address?.let { "'$it'" } ?: "NULL"
            val cityValue = location.city?.let { "'$it'" } ?: "NULL"

            val query = """
            INSERT INTO Locations (Code, Address, City, CreatedBy, IsCompanyOffice) 
            VALUES ('${location.code}', $addressValue, $cityValue, ${location.createdBy}, ${location.isCompanyOffice})
        """.trimIndent()
            return api.executeInsert(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in createLocation", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateLocation(location: Location): Response<SqlQueryResponse<Unit>> {
        return try {
            val addressValue = location.address?.let { "Address = '$it'" } ?: "Address = NULL"
            val cityValue = location.city?.let { "City = '$it'" } ?: "City = NULL"

            val query = """
            UPDATE Locations SET 
            Code = '${location.code}', 
            $addressValue,
            $cityValue, 
            CreatedBy = ${location.createdBy},
            IsCompanyOffice = ${location.isCompanyOffice}
            WHERE Id = ${location.id}
        """.trimIndent()
            return api.executeUpdate(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in updateLocation", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteLocation(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = "DELETE FROM Locations WHERE Id = $id"
            return api.executeDelete(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("LocationRepository", "Exception in deleteLocation", e)
            Response.error(500, null)
        }
    }
}