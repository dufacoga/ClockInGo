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
import androidx.room.Room.databaseBuilder
import com.example.clockingo.data.local.SessionManager
import com.example.clockingo.data.local.ThemePreferenceManager
import com.example.clockingo.data.local.AppDatabase
import com.example.clockingo.data.local.NetworkConnectivityObserver
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
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        val database = databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "clockingo.db"
        ).build()

        val roleRepository = RoleRepository(database.roleDao(), connectivityObserver)
        val roleViewModel = RoleViewModel(
            GetAllRolesUseCase(roleRepository),
            GetRoleByIdUseCase(roleRepository),
            CreateRoleUseCase(roleRepository),
            UpdateRoleUseCase(roleRepository),
            DeleteRoleUseCase(roleRepository)
        )
        val userRepository = UserRepository(database.userDao(), connectivityObserver)
        val userViewModel = UserViewModel(
            GetAllUsersUseCase(userRepository),
            GetUserByIdUseCase(userRepository),
            GetUserByUserUseCase(userRepository),
            CreateUserUseCase(userRepository),
            UpdateUserUseCase(userRepository),
            DeleteUserUseCase(userRepository),
            sessionManager,
            connectivityObserver
        )

        val locationRepository = LocationRepository(database.locationDao(), connectivityObserver)
        val locationViewModel = LocationViewModel(
            GetAllLocationsUseCase(locationRepository),
            GetLocationByIdUseCase(locationRepository),
            GetLocationByCodeUseCase(locationRepository),
            CreateLocationUseCase(locationRepository),
            UpdateLocationUseCase(locationRepository),
            DeleteLocationUseCase(locationRepository)
        )

        val entryRepository = EntryRepository(applicationContext, database.entryDao(), connectivityObserver)
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
            val isOnline by userViewModel.isOnline.collectAsState(initial = true)
            val selectedTheme by themePrefs.selectedTheme.collectAsState(initial = ClockInGoThemeOption.Green)
            val selectedThemeMode by themePrefs.selectedMode.collectAsState(initial = ThemeMode.System)
            val coroutineScope = rememberCoroutineScope()
            val currentUser by userViewModel.currentUser.collectAsState()

            LaunchedEffect(Unit) {
                try {
                    roleViewModel.loadRoles()
                    userViewModel.checkIfUserIsLoggedIn()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al cargar roles iniciales: ${e.message}", e)
                }
            }

            ClockInGoThemeWrapper(
                selectedTheme = selectedTheme,
                themeMode = selectedThemeMode
            ) {
                when {
                    isLoggedIn == true && currentUser != null -> {
                        HomeScreen(
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
                            entryViewModel = entryViewModel,
                            isOnline = isOnline
                        )
                    }

                    isLoggedIn == false -> {
                        LoginScreen(viewModel = userViewModel)
                    }

                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}