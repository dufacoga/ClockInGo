package com.example.clockingo.domain.repository

import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.domain.model.Role
import retrofit2.Response

interface IRoleRepository {
    suspend fun getAllRoles(): Response<List<Role>>
    suspend fun getRoleById(id: Int): Response<Role?>
    suspend fun createRole(role: Role): Response<SqlQueryResponse<Unit>>
    suspend fun updateRole(role: Role): Response<SqlQueryResponse<Unit>>
    suspend fun deleteRole(id: Int): Response<SqlQueryResponse<Unit>>
}