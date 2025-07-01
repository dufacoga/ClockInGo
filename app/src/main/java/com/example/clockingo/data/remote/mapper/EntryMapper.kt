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
    isSynced = IsSynced,
    deviceId = DeviceId
)

fun Entry.toDto(): EntryDto = EntryDto(
    Id = id,
    UserId = userId,
    LocationId = locationId,
    EntryTime = entryTime,
    Selfie = selfie,
    UpdatedAt = updatedAt,
    IsSynced = isSynced,
    DeviceId = deviceId
)