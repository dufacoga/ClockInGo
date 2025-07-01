package com.example.clockingo.data.remote.mapper

import com.example.clockingo.data.remote.model.RoleDto
import com.example.clockingo.domain.model.Role

fun RoleDto.toDomain(): Role = Role(
    id = Id,
    name = Name
)

fun Role.toDto(): RoleDto = RoleDto(
    Id = id,
    Name = name
)