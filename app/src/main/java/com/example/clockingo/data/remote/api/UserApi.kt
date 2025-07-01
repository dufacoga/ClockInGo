package com.example.clockingo.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.clockingo.data.remote.model.UserDto

interface UserApi {
    @Headers("Content-Type: application/json")
    @POST("/api/SqlQuery/execute")
    suspend fun executeQuery(@Body request: SqlQueryRequest): Response<List<UserDto>>

    @Headers("Content-Type: application/json")
    @POST("/api/SqlQuery/execute")
    suspend fun executeNonQuery(@Body request: SqlQueryRequest): Response<SqlQueryResponse<Unit>>
}