package com.example.clockingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.clockingo.presentation.login.LoginScreen
import com.example.clockingo.presentation.home.HomeScreen
import com.example.clockingo.ui.theme.ClockInGoThemeOption
import com.example.clockingo.ui.theme.ClockInGoThemeWrapper
import com.example.clockingo.ui.theme.ThemeMode
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var selectedTheme by remember { mutableStateOf(ClockInGoThemeOption.Green) }
            var selectedThemeMode by remember { mutableStateOf(ThemeMode.System) }

            ClockInGoThemeWrapper(
                selectedTheme = selectedTheme,
                themeMode = selectedThemeMode
            ) {
                if (isLoggedIn) {
                    HomeScreen(
                        onLogout = { isLoggedIn = false },
                        onThemeChange = { newTheme: ClockInGoThemeOption -> selectedTheme = newTheme },
                        onModeChange = { newMode: ThemeMode -> selectedThemeMode = newMode },
                        selectedTheme = selectedTheme,
                        selectedMode = selectedThemeMode
                    )
                } else {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                }
            }
        }
    }
}