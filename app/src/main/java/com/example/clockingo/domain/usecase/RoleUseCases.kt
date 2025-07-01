package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.Role
import com.example.clockingo.domain.repository.IRoleRepository
import retrofit2.Response

class GetAllRolesUseCase(private val repository: IRoleRepository) {
    suspend operator fun invoke(): Response<List<Role>> = repository.getAllRoles()
}

class GetRoleByIdUseCase(private val repository: IRoleRepository) {
    suspend operator fun invoke(id: Int): Response<Role?> = repository.getRoleById(id)
}

class CreateRoleUseCase(private val repository: IRoleRepository) {
    suspend operator fun invoke(role: Role): Response<Unit> {
        val response = repository.createRole(role)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class UpdateRoleUseCase(private val repository: IRoleRepository) {
    suspend operator fun invoke(role: Role): Response<Unit> {
        val response = repository.updateRole(role)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class DeleteRoleUseCase(private val repository: IRoleRepository) {
    suspend operator fun invoke(id: Int): Response<Unit> {
        val response = repository.deleteRole(id)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}