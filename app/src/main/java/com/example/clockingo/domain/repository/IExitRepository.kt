package com.example.clockingo.domain.repository

import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.domain.model.Exit
import retrofit2.Response

interface IExitRepository {
    suspend fun getAllExits(): Response<List<Exit>>
    suspend fun getExitById(id: Int): Response<Exit?>
    suspend fun createExit(exit: Exit): Response<SqlQueryResponse<Unit>>
    suspend fun updateExit(exit: Exit): Response<SqlQueryResponse<Unit>>
    suspend fun deleteExit(id: Int): Response<SqlQueryResponse<Unit>>
}