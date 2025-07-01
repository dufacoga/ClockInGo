package com.example.clockingo.domain.repository

import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.domain.model.User
import retrofit2.Response

interface IUserRepository {
    suspend fun getAllUsers(): Response<List<User>>
    suspend fun getUserById(id: Int): Response<User?>
    suspend fun getUserByUser(username: String, password: String): Boolean
    suspend fun createUser(user: User): Response<SqlQueryResponse<Unit>>
    suspend fun updateUser(user: User): Response<SqlQueryResponse<Unit>>
    suspend fun deleteUser(id: Int): Response<SqlQueryResponse<Unit>>
}