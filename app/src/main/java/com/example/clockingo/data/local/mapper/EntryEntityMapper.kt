package com.example.clockingo.data.local.mapper

import android.util.Base64
import com.example.clockingo.data.local.model.EntryEntity
import com.example.clockingo.domain.model.Entry

fun EntryEntity.toDomain(): Entry = Entry(
    id = id,
    userId = userId,
    locationId = locationId,
    entryTime = entryTime,
    selfie = selfie,
    updatedAt = updatedAt,
    isSynced = isSynced,
    deviceId = deviceId
)

fun Entry.toEntity(): EntryEntity = EntryEntity(
    id = id,
    userId = userId,
    locationId = locationId,
    entryTime = entryTime,
    selfie = selfie,
    updatedAt = updatedAt,
    isSynced = isSynced,
    deviceId = deviceId
)