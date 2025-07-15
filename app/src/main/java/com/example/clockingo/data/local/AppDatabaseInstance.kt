package com.example.clockingo.data.local

import android.content.Context

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