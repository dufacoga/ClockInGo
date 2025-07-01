package com.example.clockingo.data.remote.mapper

import com.example.clockingo.data.remote.model.ExitDto
import com.example.clockingo.domain.model.Exit

fun ExitDto.toDomain(): Exit = Exit(
    id = Id,
    userId = UserId,
    locationId = LocationId,
    exitTime = ExitTime,
    entryId = EntryId,
    result = Result,
    irregularBehavior = IrregularBehavior,
    reviewedByAdmin = ReviewedByAdmin,
    updatedAt = UpdatedAt,
    isSynced = IsSynced,
    deviceId = DeviceId
)

fun Exit.toDto(): ExitDto = ExitDto(
    Id = id,
    UserId = userId,
    LocationId = locationId,
    ExitTime = exitTime,
    EntryId = entryId,
    Result = result,
    IrregularBehavior = irregularBehavior,
    ReviewedByAdmin = reviewedByAdmin,
    UpdatedAt = updatedAt,
    IsSynced = isSynced,
    DeviceId = deviceId
)