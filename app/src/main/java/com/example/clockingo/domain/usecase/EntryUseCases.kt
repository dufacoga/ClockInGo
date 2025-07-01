package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.repository.IEntryRepository
import retrofit2.Response

class GetAllEntriesUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(): Response<List<Entry>> = repository.getAllEntries()
}

class GetEntryByIdUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(id: Int): Response<Entry?> = repository.getEntryById(id)
}

class CreateEntryUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(entry: Entry): Response<Unit> {
        val response = repository.createEntry(entry)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class UpdateEntryUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(entry: Entry): Response<Unit> {
        val response = repository.updateEntry(entry)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class DeleteEntryUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(id: Int): Response<Unit> {
        val response = repository.deleteEntry(id)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}