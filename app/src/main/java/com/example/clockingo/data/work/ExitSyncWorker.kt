package com.example.clockingo.data.work

import android.content.Context
import androidx.work.*
import com.example.clockingo.data.local.AppDatabaseInstance
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.model.api.InsertDto
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExitSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(
    appContext,
    params
) {
    override suspend fun doWork(): Result {
        val db = AppDatabaseInstance.getDatabase(applicationContext)
        val unsyncedExits = db.exitDao().getUnsyncedExits()
        val api = RetrofitInstance.exitApi

        unsyncedExits.forEach { entity ->
            try {
                val exit = entity.toDomain()
                val dto = InsertDto(
                    table = "Exits",
                    values = mapOf(
                        "UserId" to JsonPrimitive(exit.userId),
                        "LocationId" to JsonPrimitive(exit.locationId),
                        "ExitTime" to JsonPrimitive(exit.exitTime),
                        "EntryId" to JsonPrimitive(exit.entryId),
                        "Result" to JsonPrimitive(exit.result ?: ""),
                        "IrregularBehavior" to JsonPrimitive(exit.irregularBehavior),
                        "ReviewedByAdmin" to JsonPrimitive(exit.reviewedByAdmin),
                        "UpdatedAt" to JsonPrimitive(exit.updatedAt ?: ""),
                        "IsSynced" to JsonPrimitive(true),
                        "DeviceId" to JsonPrimitive(exit.deviceId ?: "")
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    api.insert(dto)
                }

                if (response.isSuccessful) {
                    db.exitDao().markAsSynced(entity.id)
                }
            } catch (_: Exception) {
            }
        }
        return Result.success()
    }
}

fun scheduleExitSync(context: Context) {
    val request = OneTimeWorkRequestBuilder<ExitSyncWorker>()
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "exit_sync",
        ExistingWorkPolicy.KEEP,
        request
    )
}