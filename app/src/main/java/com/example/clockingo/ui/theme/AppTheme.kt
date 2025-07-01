package com.example.clockingo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.clockingo.ui.theme.green.GreenTheme
import com.example.clockingo.ui.theme.blue.BlueTheme
import com.example.clockingo.ui.theme.red.RedTheme
import com.example.clockingo.ui.theme.yellow.YellowTheme

enum class ThemeMode {
    Light, Dark, System
}
enum class ClockInGoThemeOption {
    Blue, Green, Red, Yellow
}

@Composable
fun ClockInGoThemeWrapper(
    selectedTheme: ClockInGoThemeOption,
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    when (selectedTheme) {
        ClockInGoThemeOption.Green -> GreenTheme(darkTheme = isDarkTheme, content = content)
        ClockInGoThemeOption.Blue -> BlueTheme(darkTheme = isDarkTheme, content = content)
        ClockInGoThemeOption.Red -> RedTheme(darkTheme = isDarkTheme, content = content)
        ClockInGoThemeOption.Yellow -> YellowTheme(darkTheme = isDarkTheme, content = content)
    }
}