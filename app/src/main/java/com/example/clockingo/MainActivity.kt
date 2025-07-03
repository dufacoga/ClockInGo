package com.example.clockingo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import com.example.clockingo.presentation.login.LoginScreen
import com.example.clockingo.presentation.home.HomeScreen
import com.example.clockingo.ui.theme.ClockInGoThemeOption
import com.example.clockingo.ui.theme.ClockInGoThemeWrapper
import com.example.clockingo.ui.theme.ThemeMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.clockingo.data.local.SessionManager
import com.example.clockingo.data.repository.RoleRepository
import com.example.clockingo.data.repository.UserRepository
import com.example.clockingo.domain.usecase.*
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.clockingo.presentation.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRepository = UserRepository()
        val sessionManager = SessionManager(applicationContext)
        val userViewModel = UserViewModel(
            GetAllUsersUseCase(userRepository),
            GetUserByIdUseCase(userRepository),
            GetUserByUserUseCase(userRepository),
            CreateUserUseCase(userRepository),
            UpdateUserUseCase(userRepository),
            DeleteUserUseCase(userRepository),
            sessionManager
        )
        val roleRepository = RoleRepository()
        val roleViewModel = RoleViewModel(
            GetAllRolesUseCase(roleRepository),
            GetRoleByIdUseCase(roleRepository),
            CreateRoleUseCase(roleRepository),
            UpdateRoleUseCase(roleRepository),
            DeleteRoleUseCase(roleRepository)
        )

        setContent {
            val isLoggedIn by userViewModel.loggedIn.collectAsState()
            var selectedTheme by remember { mutableStateOf(ClockInGoThemeOption.Green) }
            var selectedThemeMode by remember { mutableStateOf(ThemeMode.System) }
            LaunchedEffect(Unit) {
                userViewModel.checkIfUserIsLoggedIn()
            }

            ClockInGoThemeWrapper(
                selectedTheme = selectedTheme,
                themeMode = selectedThemeMode
            ) {
                when (isLoggedIn) {
                    true -> HomeScreen(
                        onThemeChange = { newTheme -> selectedTheme = newTheme },
                        onModeChange = { newMode -> selectedThemeMode = newMode },
                        selectedTheme = selectedTheme,
                        selectedMode = selectedThemeMode,
                        userViewModel = userViewModel,
                        roleViewModel = roleViewModel
                    )

                    false -> LoginScreen(
                        viewModel = userViewModel
                    )

                    null -> { }
                }
            }
        }
    }
}