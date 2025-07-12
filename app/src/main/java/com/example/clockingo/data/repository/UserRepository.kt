package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.repository.IUserRepository
import com.google.gson.JsonPrimitive
import retrofit2.Response

class UserRepository : IUserRepository {
    private val api = RetrofitInstance.userApi

    override suspend fun getAllUsers(): Response<List<User>> {
        return try {
            val dto = SelectDto(table = "Users")
            val response = api.select(dto)
            val usersDto = response.body() ?: emptyList()
            val users = usersDto.map { it.toDomain() }
            return Response.success(users)
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getUserById(id: Int): Response<User?> {
        return try {
            val dto = SelectDto(
                table = "Users",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.select(dto)
            val userDto = response.body() ?.firstOrNull()
            return Response.success(userDto?.toDomain())
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getUserByUser(username: String, password: String): Boolean {
        return try {
            val dto = SelectDto(
                table = "Users",
                where = mapOf("Username" to JsonPrimitive(username))
            )
            val response = api.select(dto)
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                val usersDto = response.body() ?: emptyList()
                if (usersDto.isNotEmpty()) {
                    val user = usersDto.first().toDomain()
                    return user.authToken == password
                }
            }
            false
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            false
        }
    }

    override suspend fun createUser(user: User): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = InsertDto(
                table = "Users",
                values = mapOf(
                    "Name" to JsonPrimitive(user.name),
                    "Phone" to JsonPrimitive(user.phone ?: ""),
                    "Username" to JsonPrimitive(user.username),
                    "AuthToken" to JsonPrimitive(user.authToken),
                    "RoleId" to JsonPrimitive(user.roleId)
                )
            )
            api.insert(dto)
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateUser(user: User): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = UpdateDto(
                table = "Users",
                set = mapOf(
                    "Name" to JsonPrimitive(user.name),
                    "Phone" to JsonPrimitive(user.phone ?: ""),
                    "Username" to JsonPrimitive(user.username),
                    "AuthToken" to JsonPrimitive(user.authToken),
                    "RoleId" to JsonPrimitive(user.roleId)
                ),
                where = mapOf("Id" to JsonPrimitive(user.id))
            )
            api.update(dto)
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteUser(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val dto = DeleteDto(
                table = "Users",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            api.delete(dto)
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }
}