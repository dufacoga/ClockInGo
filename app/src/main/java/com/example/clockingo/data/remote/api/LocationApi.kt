package com.example.clockingo.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import com.example.clockingo.data.remote.model.LocationDto
import com.example.clockingo.data.remote.model.api.SelectDto
import com.example.clockingo.data.remote.model.api.InsertDto
import com.example.clockingo.data.remote.model.api.UpdateDto
import com.example.clockingo.data.remote.model.api.DeleteDto

interface LocationApi {
    @Headers("Content-Type: application/json")
    @POST("/api/SqlQuery/select")
    suspend fun select(@Body request: SelectDto): Response<List<LocationDto>>

    @Headers("Content-Type: application/json")
    @POST("/api/SqlQuery/insert")
    suspend fun insert(@Body request: InsertDto): Response<SqlQueryResponse<Unit>>

    @Headers("Content-Type: application/json")
    @PUT("/api/SqlQuery/update")
    suspend fun update(@Body request: UpdateDto): Response<SqlQueryResponse<Unit>>

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/api/SqlQuery/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteDto): Response<SqlQueryResponse<Unit>>
}