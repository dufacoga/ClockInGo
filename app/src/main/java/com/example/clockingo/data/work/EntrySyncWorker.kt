package com.example.clockingo.data.work

import android.content.Context
import androidx.work.*
import com.example.clockingo.data.local.AppDatabaseInstance
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.model.api.InsertDto
import com.google.gson.JsonPrimitive

class EntrySyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(
    appContext,
    params
) {
    override suspend fun doWork(): Result {
        val db = AppDatabaseInstance.getDatabase(applicationContext)
        val unsynced = db.entryDao().getUnsyncedEntries()
        val api = RetrofitInstance.entryApi
        unsynced.forEach { entity ->
            try {
                val entry = entity.toDomain()
                val cleanedSelfie = try {
//                    Code tested for MySQL
//                    val innerBase64 = String(
//                        android.util.Base64.decode(entry.selfie ?: "", android.util.Base64.NO_WRAP),
//                        Charsets.UTF_8
//                    )
                    val innerBase64 = entry.selfie ?: ""
                    innerBase64
                } catch (e: Exception) {
                    entry.selfie ?: ""
                }
                val dto = InsertDto(
                    table = "Entries",
                    values = mapOf(
                        "UserId" to JsonPrimitive(entry.userId),
                        "LocationId" to JsonPrimitive(entry.locationId),
                        "EntryTime" to JsonPrimitive(entry.entryTime),
                        "Selfie" to JsonPrimitive(cleanedSelfie),
                        "UpdatedAt" to JsonPrimitive(entry.updatedAt ?: ""),
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(entry.deviceId)
                    )
                )
                val response = api.insert(dto)
                if (response.isSuccessful) {
                    db.entryDao().markAsSynced(entity.id)
                }
            } catch (_: Exception) {
            }
        }
        return Result.success()
    }
}

fun scheduleEntrySync(context: Context) {
    val request = OneTimeWorkRequestBuilder<EntrySyncWorker>()
        .setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        )
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "entry_sync",
        ExistingWorkPolicy.KEEP,
        request
    )
}