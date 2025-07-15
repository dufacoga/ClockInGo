package com.example.clockingo.data.work

import android.content.Context
import androidx.work.*
import com.example.clockingo.data.local.AppDatabase
import com.example.clockingo.data.local.mapper.toDomain
import com.example.clockingo.data.remote.api.RetrofitInstance
import com.example.clockingo.data.remote.model.api.InsertDto
import com.google.gson.JsonPrimitive

class EntrySyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val db = AppDatabaseInstance.getDatabase(applicationContext)
        val unsynced = db.entryDao().getUnsyncedEntries()
        val api = RetrofitInstance.entryApi
        unsynced.forEach { entity ->
            try {
                val entry = entity.toDomain()
                val dto = InsertDto(
                    table = "Entries",
                    values = mapOf(
                        "UserId" to JsonPrimitive(entry.userId),
                        "LocationId" to JsonPrimitive(entry.locationId),
                        "EntryTime" to JsonPrimitive(entry.entryTime),
                        "Selfie" to JsonPrimitive(entry.selfie ?: ""),
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

object AppDatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = androidx.room.Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "clockingo.db"
            ).build()
            INSTANCE = instance
            instance
        }
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