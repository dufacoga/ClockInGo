package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryRequest
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.repository.IUserRepository
import retrofit2.Response

class UserRepository : IUserRepository {
    private val api = RetrofitInstance.userApi

    override suspend fun getAllUsers(): Response<List<User>> {
        return try {
            val query = "SELECT * FROM Users"
            val response = api.executeSelect(query)
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
            val query = "SELECT * FROM Users WHERE id = $id"
            val response = api.executeSelect(query)
            val userDto = response.body() ?.firstOrNull()
            return Response.success(userDto?.toDomain())
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            return Response.error(500, null)
        }
    }

    override suspend fun getUserByUser(username: String, password: String): Boolean {
        return try {
            val query = "SELECT * FROM Users WHERE Username = '$username'"
            val response = api.executeSelect(query)
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
            val query = """
            INSERT INTO Users (Name, Phone, Username, AuthToken, RoleId) 
            VALUES ('${user.name}', '${user.phone}', '${user.username}', '${user.authToken}', ${user.roleId})
        """.trimIndent()
            return api.executeInsert(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }

    override suspend fun updateUser(user: User): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = """
            UPDATE Users SET 
            Name = '${user.name}', 
            Phone = '${user.phone}',
            Username = '${user.username}',
            AuthToken = '${user.authToken}', 
            RoleId = '${user.roleId}'
            WHERE Id = ${user.id}
        """.trimIndent()
            return api.executeUpdate(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }

    override suspend fun deleteUser(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            val query = "DELETE FROM Users WHERE id = $id"
            return api.executeDelete(SqlQueryRequest(query))
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            Response.error(500, null)
        }
    }
}