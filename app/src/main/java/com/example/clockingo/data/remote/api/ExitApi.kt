package com.example.clockingo.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import com.example.clockingo.data.remote.model.ExitDto

interface ExitApi {
    @Headers("Content-Type: application/json")
    @GET("/api/SqlQuery/execute")
    suspend fun executeSelect(@Query("query") query: String): Response<List<ExitDto>>

    @Headers("Content-Type: application/json")
    @POST("/api/SqlQuery/execute")
    suspend fun executeInsert(@Body request: SqlQueryRequest): Response<SqlQueryResponse<Unit>>

    @Headers("Content-Type: application/json")
    @PUT("/api/SqlQuery/execute")
    suspend fun executeUpdate(@Body request: SqlQueryRequest): Response<SqlQueryResponse<Unit>>

    @Headers("Content-Type: application/json")
    @DELETE("/api/SqlQuery/execute")
    suspend fun executeDelete(@Body request: SqlQueryRequest): Response<SqlQueryResponse<Unit>>
}