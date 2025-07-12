package com.example.clockingo.domain.repository

import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.domain.model.Location
import retrofit2.Response

interface ILocationRepository {
    suspend fun getAllLocations(): Response<List<Location>>
    suspend fun getLocationById(id: Int): Response<Location?>
    suspend fun getLocationByCode(id: String): Response<Location?>
    suspend fun createLocation(location: Location): Response<SqlQueryResponse<Unit>>
    suspend fun updateLocation(location: Location): Response<SqlQueryResponse<Unit>>
    suspend fun deleteLocation(id: Int): Response<SqlQueryResponse<Unit>>
}