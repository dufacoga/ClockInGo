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
    irregularBehavior = IrregularBehavior == 1,
//    Code tested for MySQL
//    irregularBehavior = IrregularBehavior,
    reviewedByAdmin = ReviewedByAdmin == 1,
//    Code tested for MySQL
//    reviewedByAdmin = ReviewedByAdmin,
    updatedAt = UpdatedAt,
    isSynced = IsSynced == 1,
//    Code tested for MySQL
//    isSynced = IsSynced,
    deviceId = DeviceId
)

fun Exit.toDto(): ExitDto = ExitDto(
    Id = id,
    UserId = userId,
    LocationId = locationId,
    ExitTime = exitTime,
    EntryId = entryId,
    Result = result,
    IrregularBehavior = if (irregularBehavior) 1 else 0,
//    Code tested for MySQL
//    IrregularBehavior = irregularBehavior,
    ReviewedByAdmin = if (reviewedByAdmin) 1 else 0,
//    Code tested for MySQL
//    ReviewedByAdmin = reviewedByAdmin,
    UpdatedAt = updatedAt,
    IsSynced = if (isSynced) 1 else 0,
//    Code tested for MySQL
//    IsSynced = isSynced,
    DeviceId = deviceId
)