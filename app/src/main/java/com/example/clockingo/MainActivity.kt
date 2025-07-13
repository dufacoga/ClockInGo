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
import com.example.clockingo.data.local.SessionManager
import com.example.clockingo.data.local.ThemePreferenceManager
import com.example.clockingo.data.repository.EntryRepository
import com.example.clockingo.data.repository.LocationRepository
import com.example.clockingo.data.repository.RoleRepository
import com.example.clockingo.data.repository.UserRepository
import com.example.clockingo.domain.usecase.*
import com.example.clockingo.presentation.viewmodel.EntryViewModel
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.clockingo.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val themePrefs = ThemePreferenceManager(applicationContext)

        val userRepository = UserRepository()
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

        val locationRepository = LocationRepository()
        val locationViewModel = LocationViewModel(
            GetAllLocationsUseCase(locationRepository),
            GetLocationByIdUseCase(locationRepository),
            GetLocationByCodeUseCase(locationRepository),
            CreateLocationUseCase(locationRepository),
            UpdateLocationUseCase(locationRepository),
            DeleteLocationUseCase(locationRepository)
        )

        val entryRepository = EntryRepository()
        val entryViewModel = EntryViewModel(
            GetAllEntriesUseCase(entryRepository),
            GetEntryByIdUseCase(entryRepository),
            HasRecentEntryUseCase(entryRepository),
            CreateEntryUseCase(entryRepository),
            UpdateEntryUseCase(entryRepository),
            DeleteEntryUseCase(entryRepository)
        )

        setContent {
            val isLoggedIn by userViewModel.loggedIn.collectAsState()
            val selectedTheme by themePrefs.selectedTheme.collectAsState(initial = ClockInGoThemeOption.Green)
            val selectedThemeMode by themePrefs.selectedMode.collectAsState(initial = ThemeMode.System)
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                userViewModel.checkIfUserIsLoggedIn()
            }

            ClockInGoThemeWrapper(
                selectedTheme = selectedTheme,
                themeMode = selectedThemeMode
            ) {
                when (isLoggedIn) {
                    true -> HomeScreen(
                        onThemeChange = { newTheme ->
                            coroutineScope.launch {
                                themePrefs.saveTheme(newTheme)
                            }
                        },
                        onModeChange = { newMode ->
                            coroutineScope.launch {
                                themePrefs.saveMode(newMode)
                            }
                        },
                        selectedTheme = selectedTheme,
                        selectedMode = selectedThemeMode,
                        userViewModel = userViewModel,
                        roleViewModel = roleViewModel,
                        locationViewModel = locationViewModel,
                        entryViewModel = entryViewModel
                    )

                    false -> LoginScreen(viewModel = userViewModel)

                    null -> {}
                }
            }
        }
    }
}