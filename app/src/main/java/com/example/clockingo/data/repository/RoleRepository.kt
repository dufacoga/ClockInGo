package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.local.ConnectivityObserver
import com.example.clockingo.data.local.dao.RoleDao
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.local.mapper.toEntity
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain as DtoToDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.repository.IRoleRepository
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response

class RoleRepository(
    private val dao: RoleDao,
    private val connectivityObserver: ConnectivityObserver
) : IRoleRepository {
    private val api = RetrofitInstance.roleApi

    override suspend fun getAllRoles(): Response<List<Role>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(table = "Roles")
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val rolesDto = apiResponse.body() ?: emptyList()
                    val roles = rolesDto.map { it.DtoToDomain() }
                    dao.deleteAllRoles()
                    roles.forEach { dao.insert(it.toEntity()) }
                    Response.success(roles)
                } else {
                    Log.w("RoleRepository", "API failed for getAllRoles (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getAllRoles().map { it.toDomain() })
                }
            } else {
                Log.i("RoleRepository", "No internet, loading getAllRoles from local DB.")
                Response.success(dao.getAllRoles().map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in getAllRoles", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getRoleById(id: Int): Response<Role?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Roles",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val roleDto = apiResponse.body()?.firstOrNull()
                    val role = roleDto?.DtoToDomain()
                    role?.let { dao.insert(it.toEntity()) }
                    Response.success(role)
                } else {
                    Log.w("RoleRepository", "API failed for getRoleById (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getRoleById(id)?.toDomain())
                }
            } else {
                Log.i("RoleRepository", "No internet, loading getRoleById from local DB.")
                Response.success(dao.getRoleById(id)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in getRoleById", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun createRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = InsertDto(
                table = "Roles",
                values = mapOf("Name" to JsonPrimitive(role.name))
            )
            val response = api.insert(dto)
            if (response.isSuccessful) {
                dao.insert(role.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in createRole", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun updateRole(role: Role): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = UpdateDto(
                table = "Roles",
                set = mapOf("Name" to JsonPrimitive(role.name)),
                where = mapOf("Id" to JsonPrimitive(role.id))
            )
            val response = api.update(dto)
            if (response.isSuccessful) {
                dao.insert(role.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in updateRole", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun deleteRole(id: Int): Response<SqlQueryResponse<Unit>> {
        if (!connectivityObserver.currentStatus()) {
            val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
            return Response.error(503, errorBody)
        }
        return try {
            val dto = DeleteDto(
                table = "Roles",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.delete(dto)
            if (response.isSuccessful) {
                val roleEntity = dao.getRoleById(id)
                roleEntity?.let { dao.delete(it) }
            }
            response
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception in deleteRole", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }
}