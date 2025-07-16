package com.example.clockingo.data.remote.mapper

import com.example.clockingo.data.remote.model.EntryDto
import com.example.clockingo.domain.model.Entry

fun EntryDto.toDomain(): Entry = Entry(
    id = Id,
    userId = UserId,
    locationId = LocationId,
    entryTime = EntryTime,
    selfie = Selfie,
    updatedAt = UpdatedAt,
//    Code tested for MySQL
//    IsSynced = isSynced,
    isSynced = IsSynced == 1,
    deviceId = DeviceId
)

fun Entry.toDto(): EntryDto = EntryDto(
    Id = id,
    UserId = userId,
    LocationId = locationId,
    EntryTime = entryTime,
    Selfie = selfie,
    UpdatedAt = updatedAt,
//    Code tested for MySQL
//    IsSynced = isSynced,
    IsSynced = if (isSynced) 1 else 0,
    DeviceId = deviceId
)