package com.example.clockingo.presentation.components

data class DrawerMenuItem(
    val id: Int,
    val title: String,
    val subItems: List<DrawerSubItems> = emptyList(),
    val isVisible: Boolean = true
)

data class DrawerSubItems(
    val id: Int,
    val title: String,
    val isVisible: Boolean = true
)