package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryRequest
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.repository.IRoleRepository
import retrofit2.Response

class RoleRepository : IRoleRepository {
    private val api = RetrofitInstance.roleApi

    override suspend fun getAllRoles(): Response<List<Role>> {
        return try {
            val query = "SELECT * FROM Roles"
            val response = api.executeQuery(SqlQueryRequest(query))
            val rolesDto = response.body() ?: emptyList()
            val roles = rolesDto.map { it.toDomain() }
            return Response.success(roles)
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in getAllRoles", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getRoleById(id: Int): Response<Role?> {
        return try {
            val query = "SELECT * FROM Roles WHERE Id = $id"
            val response = api.executeQuery(SqlQueryRequest(query))
            val roleDto = response.body()?.firstOrNull()
            return Response.success(roleDto?.toDomain())
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in getRoleById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = """
            INSERT INTO Roles (Name) 
            VALUES ('${role.name}')
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in createRole", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = """
            UPDATE Roles SET 
            Name = '${role.name}'
            WHERE Id = ${role.id}
        """.trimIndent()
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in updateRole", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteRole(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = "DELETE FROM Roles WHERE Id = $id"
            return api.executeNonQuery(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in deleteRole", e)
            Response.error(500, null)
        }
    }
}