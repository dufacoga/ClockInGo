package com.example.clockingo.data.repository

import android.util.Log
import com.example.clockingo.data.local.ConnectivityObserver
import com.example.clockingo.data.local.dao.UserDao
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.local.mapper.toEntity
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.data.remote.mapper.toDomain as DtoToDomain
import com.example.clockingo.data.remote.model.api.*
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.repository.IUserRepository
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Response

class UserRepository(
    private val dao: UserDao,
    private val connectivityObserver: ConnectivityObserver
) : IUserRepository {
    private val api = RetrofitInstance.userApi

    override suspend fun getAllUsers(): Response<List<User>> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(table = "Users")
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val usersDto = apiResponse.body() ?: emptyList()
                    val users = usersDto.map { it.DtoToDomain() }
                    dao.deleteAllUsers()
                    users.forEach { dao.insert(it.toEntity()) }
                    Response.success(users)
                } else {
                    Log.w("UserRepository", "API failed for getAllUsers (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getAllUsers().map { it.toDomain() })
                }
            } else {
                Log.i("UserRepository", "No internet, loading getAllUsers from local DB.")
                Response.success(dao.getAllUsers().map { it.toDomain() })
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getUserById(id: Int): Response<User?> {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Users",
                    where = mapOf("Id" to JsonPrimitive(id))
                )
                val apiResponse = api.select(dto)
                if (apiResponse.isSuccessful) {
                    val userDto = apiResponse.body()?.firstOrNull()
                    val user = userDto?.DtoToDomain()
                    user?.let { dao.insert(it.toEntity()) }
                    Response.success(user)
                } else {
                    Log.w("UserRepository", "API failed for getUserById (${apiResponse.code()}), loading from local DB.")
                    Response.success(dao.getUserById(id)?.toDomain())
                }
            } else {
                Log.i("UserRepository", "No internet, loading getUserById from local DB.")
                Response.success(dao.getUserById(id)?.toDomain())
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun getUserByUser(username: String, password: String): User? {
        return try {
            if (connectivityObserver.currentStatus()) {
                val dto = SelectDto(
                    table = "Users",
                    where = mapOf("Username" to JsonPrimitive(username))
                )
                val response = api.select(dto)
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val usersDto = response.body() ?: emptyList()
                    val user = usersDto.first().DtoToDomain()
                    if (user.authToken == password) {
                        dao.insert(user.toEntity())
                        return user
                    }
                    return null
                }
                null
            } else {
                Log.i("UserRepository", "No internet, attempting offline authentication for $username.")
                val localUser = dao.getUserByUsername(username)
                return if (localUser != null && localUser.authToken == password) {
                    localUser.toDomain()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser (authentication), attempting local fallback", e)
            val localUser = dao.getUserByUsername(username)
            return if (localUser != null && localUser.authToken == password) {
                localUser.toDomain()
            } else {
                null
            }
        }
    }

    override suspend fun createUser(user: User): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
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
            val response = api.insert(dto)
            if (response.isSuccessful) {
                dao.insert(user.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun updateUser(user: User): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
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
            val response = api.update(dto)
            if (response.isSuccessful) {
                dao.insert(user.toEntity())
            }
            response
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }

    override suspend fun deleteUser(id: Int): Response<SqlQueryResponse<Unit>> {
        return try {
            if (!connectivityObserver.currentStatus()) {
                val errorBody: ResponseBody = ResponseBody.create(null, "No Internet Connection")
                return Response.error(503, errorBody)
            }
            val dto = DeleteDto(
                table = "Users",
                where = mapOf("Id" to JsonPrimitive(id))
            )
            val response = api.delete(dto)
            if (response.isSuccessful) {
                val userEntity = dao.getUserById(id)
                userEntity?.let { dao.delete(it) }
            }
            response
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in getUserByUser", e)
            val errorBody: ResponseBody = ResponseBody.create(null, "Internal Server Error")
            Response.error(500, errorBody)
        }
    }
}