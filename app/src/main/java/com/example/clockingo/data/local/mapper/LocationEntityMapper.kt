package com.example.clockingo.data.local.mapper

import com.example.clockingo.data.local.model.LocationEntity
import com.example.clockingo.domain.model.Location

fun LocationEntity.toDomain(): Location = Location(
    id = id,
    code = code,
    address = address,
    city = city,
    createdBy = createdBy,
    isCompanyOffice = isCompanyOffice
)

fun Location.toEntity(): LocationEntity = LocationEntity(
    id = id,
    code = code,
    address = address,
    city = city,
    createdBy = createdBy,
    isCompanyOffice = isCompanyOffice
)