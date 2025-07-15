package com.example.clockingo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clockingo.data.local.dao.EntryDao
import com.example.clockingo.data.local.model.*

@Database(
    entities = [EntryEntity::class, ExitEntity::class, LocationEntity::class, RoleEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}