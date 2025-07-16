package com.example.clockingo.data.remote.mapper

import com.example.clockingo.data.remote.model.LocationDto
import com.example.clockingo.domain.model.Location

fun LocationDto.toDomain(): Location = Location(
    id = Id,
    code = Code,
    address = Address,
    city = City,
    createdBy = CreatedBy,
//    Code tested for MySQL
//    isCompanyOffice = IsCompanyOffice
    isCompanyOffice = IsCompanyOffice == 1
)

fun Location.toDto(): LocationDto = LocationDto(
    Id = id,
    Code = code,
    Address = address,
    City = city,
    CreatedBy = createdBy,
//    Code tested for MySQL
//    isCompanyOffice = IsCompanyOffice
    IsCompanyOffice = if (isCompanyOffice) 1 else 0
)