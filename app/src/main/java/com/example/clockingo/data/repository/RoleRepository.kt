package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.repository.IRoleRepository
import com.google.gson.JsonPrimitive
import retrofit2.Response

class RoleRepository : IRoleRepository {
    private val api = RetrofitInstance.roleApi

    override suspend fun getAllRoles(): Response<List<Role>> {
        return try {
            val dto = SelectDto(table = "Roles")
            val response = api.select(dto)
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
            val dto = SelectDto(
                table = "Roles",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.select(dto)
            val roleDto = response.body()?.firstOrNull()
            return Response.success(roleDto?.toDomain())
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in getRoleById", e)
            return Response.error(500, null)
        }
    }

    override suspend fun createRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = InsertDto(
                table = "Roles",
                values = mapOf("Name" to JsonPrimitive(role.name))
            )
            api.insert(dto)
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in createRole", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = UpdateDto(
                table = "Roles",
                values = mapOf("Name" to JsonPrimitive(role.name)),
                where = mapOf("Id" to JsonPrimitive(role.id))
            )
            api.update(dto)
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in updateRole", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteRole(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = DeleteDto(
                table = "Roles",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            api.delete(dto)
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in deleteRole", e)
            Response.error(500, null)
        }
    }
}