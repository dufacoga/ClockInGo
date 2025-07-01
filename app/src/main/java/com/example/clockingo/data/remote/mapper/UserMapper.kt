package com.example.clockingo.data.remote.mapper

import com.example.clockingo.data.remote.model.UserDto
import com.example.clockingo.domain.model.User

fun UserDto.toDomain(): User = User(
    id = Id,
    name = Name,
    phone = Phone ?: "",
    username = Username,
    authToken = AuthToken,
    roleId = RoleId
)

fun User.toDto(): UserDto = UserDto(
    Id = id,
    Name = name,
    Phone = phone ?: "",
    Username = username,
    AuthToken = authToken,
    RoleId = roleId
)