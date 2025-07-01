package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.Exit
import com.example.clockingo.domain.repository.IExitRepository
import retrofit2.Response

class GetAllExitsUseCase(private val repository: IExitRepository) {
    suspend operator fun invoke(): Response<List<Exit>> = repository.getAllExits()
}

class GetExitByIdUseCase(private val repository: IExitRepository) {
    suspend operator fun invoke(id: Int): Response<Exit?> = repository.getExitById(id)
}

class CreateExitUseCase(private val repository: IExitRepository) {
    suspend operator fun invoke(exit: Exit): Response<Unit> {
        val response = repository.createExit(exit)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class UpdateExitUseCase(private val repository: IExitRepository) {
    suspend operator fun invoke(exit: Exit): Response<Unit> {
        val response = repository.updateExit(exit)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class DeleteExitUseCase(private val repository: IExitRepository) {
    suspend operator fun invoke(id: Int): Response<Unit> {
        val response = repository.deleteExit(id)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}