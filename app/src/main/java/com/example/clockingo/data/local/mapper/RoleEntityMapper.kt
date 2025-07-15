package com.example.clockingo.data.local.mapper

import com.example.clockingo.data.local.model.RoleEntity
import com.example.clockingo.domain.model.Role

fun RoleEntity.toDomain(): Role = Role(
    id = id,
    name = name
)

fun Role.toEntity(): RoleEntity = RoleEntity(
    id = id,
    name = name
)