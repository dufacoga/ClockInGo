package com.example.clockingo.data.local.mapper

import com.example.clockingo.data.local.model.UserEntity
import com.example.clockingo.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    phone = phone,
    username = username,
    authToken = authToken,
    roleId = roleId
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    phone = phone,
    username = username,
    authToken = authToken,
    roleId = roleId
)