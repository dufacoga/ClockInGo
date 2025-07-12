package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.Entry
import com.example.clockingo.domain.repository.IEntryRepository
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetAllEntriesUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(): Response<List<Entry>> = repository.getAllEntries()
}

class GetEntryByIdUseCase(private val repository: IEntryRepository) {
    suspend operator fun invoke(id: Int): Response<Entry?> = repository.getEntryById(id)
}

class HasRecentEntryUseCase(private val repository: IEntryRepository, private val windowMinutes: Int = 15) {
    suspend operator fun invoke(userId: Int): Boolean {
        val response = repository.getEntriesByUser(userId)
        if (!response.isSuccessful) return false

        val entries = response.body().orEmpty()

        val thresholdMillis = System.currentTimeMillis() - windowMinutes * 60_000L
        val threshold = Date(thresholdMillis)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val validEntries = entries.filterNotNull()

        return validEntries.any { entry ->
            val entryTime = entry.entryTime ?: return@any false
            val parsedDate = formatter.parse(entryTime) ?: return@any false
            parsedDate.after(threshold)
        }
    }
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