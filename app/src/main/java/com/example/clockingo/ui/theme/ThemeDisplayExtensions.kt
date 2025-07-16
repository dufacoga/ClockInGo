package com.example.clockingo.ui.theme

import com.example.clockingo.R

fun ThemeMode.toDisplayNameRes(): Int {
    return when (this) {
        ThemeMode.Light -> R.string.theme_mode_light
        ThemeMode.Dark -> R.string.theme_mode_dark
        ThemeMode.System -> R.string.theme_mode_system
    }
}

fun ClockInGoThemeOption.toDisplayNameRes(): Int {
    return when (this) {
        ClockInGoThemeOption.Blue -> R.string.theme_color_blue
        ClockInGoThemeOption.Green -> R.string.theme_color_green
        ClockInGoThemeOption.Red -> R.string.theme_color_red
        ClockInGoThemeOption.Yellow -> R.string.theme_color_yellow
    }
}