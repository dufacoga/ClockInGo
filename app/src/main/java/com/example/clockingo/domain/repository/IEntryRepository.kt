package com.example.clockingo.domain.repository

import com.example.clockingo.data.remote.api.SqlQueryResponse
import com.example.clockingo.domain.model.Entry
import retrofit2.Response

interface IEntryRepository {
    suspend fun getAllEntries(): Response<List<Entry>>
    suspend fun getEntryById(id: Int): Response<Entry?>
    suspend fun getEntriesByUser(userId: Int): Response<List<Entry>>
    suspend fun createEntry(entry: Entry): Response<SqlQueryResponse<Unit>>
    suspend fun updateEntry(entry: Entry): Response<SqlQueryResponse<Unit>>
    suspend fun deleteEntry(id: Int): Response<SqlQueryResponse<Unit>>
}