package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.repository.IUserRepository
import retrofit2.Response

class GetAllUsersUseCase(private val repository: IUserRepository) {
    suspend operator fun invoke(): Response<List<User>> = repository.getAllUsers()
}

class GetUserByIdUseCase(private val repository: IUserRepository) {
    suspend operator fun invoke(id: Int): Response<User?> = repository.getUserById(id)
}

class GetUserByUserUseCase(private val repository: IUserRepository) {
    var loggedInUser: User? = null
    suspend operator fun invoke(username: String, password: String): Boolean {
        val response = repository.getUserByUser(username, password)
        loggedInUser = response
        return response != null
    }
}

class CreateUserUseCase(private val repository: IUserRepository) {
    suspend operator fun invoke(user: User): Response<Unit> {
        val response = repository.createUser(user)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class UpdateUserUseCase(private val repository: IUserRepository) {
    suspend operator fun invoke(user: User): Response<Unit> {
        val response = repository.updateUser(user)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class DeleteUserUseCase(private val repository: IUserRepository) {
    suspend operator fun invoke(id: Int): Response<Unit> {
        val response = repository.deleteUser(id)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}