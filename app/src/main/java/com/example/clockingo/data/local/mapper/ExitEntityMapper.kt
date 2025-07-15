package com.example.clockingo.data.local.mapper

import com.example.clockingo.data.local.model.ExitEntity
import com.example.clockingo.domain.model.Exit

fun ExitEntity.toDomain(): Exit = Exit(
    id = id,
    userId = userId,
    locationId = locationId,
    exitTime = exitTime,
    entryId = entryId,
    result = result,
    irregularBehavior = irregularBehavior,
    reviewedByAdmin = reviewedByAdmin,
    updatedAt = updatedAt,
    isSynced = isSynced,
    deviceId = deviceId
)

fun Exit.toEntity(): ExitEntity = ExitEntity(
    id = id,
    userId = userId,
    locationId = locationId,
    exitTime = exitTime,
    entryId = entryId,
    result = result,
    irregularBehavior = irregularBehavior,
    reviewedByAdmin = reviewedByAdmin,
    updatedAt = updatedAt,
    isSynced = isSynced,
    deviceId = deviceId
)