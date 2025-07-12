package com.example.clockingo.presentation.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clockingo.presentation.components.DrawerMenuItem
import com.example.clockingo.presentation.components.DrawerSubItems
import com.example.clockingo.presentation.components.ShowThemeDialog
import com.example.clockingo.ui.theme.ClockInGoThemeOption
import com.example.clockingo.ui.theme.ThemeMode
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.clockingo.domain.model.User
import com.example.clockingo.presentation.home.users.CreateUsersScreen
import com.example.clockingo.presentation.home.users.FindUsersScreen
import com.example.clockingo.presentation.home.users.UpdateUsersScreen
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.clockingo.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onThemeChange: (ClockInGoThemeOption) -> Unit,
    onModeChange: (ThemeMode) -> Unit,
    selectedTheme: ClockInGoThemeOption,
    selectedMode: ThemeMode,
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuItems = listOf(
        DrawerMenuItem(0,"Home"),
        DrawerMenuItem(
            1,
            "Users",
            subItems = listOf(
                DrawerSubItems(10, "Find existing", true),
                DrawerSubItems(11, "Create new", true),
                DrawerSubItems(12, "Update existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            2,
            "Locations",
            subItems = listOf(
                DrawerSubItems(20, "Find existing", true),
                DrawerSubItems(21, "Create new", true),
                DrawerSubItems(22, "Update existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            3,
            "Entries",
            subItems = listOf(
                DrawerSubItems(30, "Add new", true),
                DrawerSubItems(31, "Find existing", true),
                DrawerSubItems(32, "Update existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            4,
            "Exits",
            subItems = listOf(
                DrawerSubItems(40, "Add new", true),
                DrawerSubItems(41, "Find existing", true),
                DrawerSubItems(42, "Update existing", true),
                DrawerSubItems(43, "Audit existing", true)
            ),
            true
        )
    )

    val UserSaver: Saver<User?, Any> = mapSaver(
        save = { user ->
            if (user == null) emptyMap()
            else mapOf(
                "id" to user.id,
                "name" to user.name,
                "phone" to user.phone,
                "username" to user.username,
                "authToken" to user.authToken,
                "roleId" to user.roleId
            )
        },
        restore = {
            if (it.isEmpty()) null
            else User(
                id = it["id"] as Int,
                name = it["name"] as String,
                phone = it["phone"] as String?,
                username = it["username"] as String,
                authToken = it["authToken"] as String,
                roleId = it["roleId"] as Int
            )
        }
    )

    var selectedMenu by rememberSaveable { mutableStateOf(0) }
    var selectedUser by rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf<User?>(null)
    }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val expandedItems = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberScrollState()

    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        selectedUser = currentUser
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.title) },
                            selected = selectedMenu == item.id,
                            onClick = {
                                if (item.subItems.isEmpty()) {
                                    selectedMenu = item.id
                                    scope.launch { drawerState.close() }
                                } else {
                                    expandedItems[item.id] = !(expandedItems[item.id] ?: false)
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        if (expandedItems[item.id] == true) {
                            item.subItems.forEach { sub ->
                                NavigationDrawerItem(
                                    label = { Text(sub.title) },
                                    selected = selectedMenu == sub.id,
                                    onClick = {
                                        selectedMenu = sub.id
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier
                                        .padding(start = 32.dp, top = 4.dp, bottom = 4.dp)
                                        .fillMaxWidth(),
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    title = { Text("ClockInGo", color = MaterialTheme.colorScheme.onPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showDropdownMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            DropdownMenu(
                                expanded = showDropdownMenu,
                                onDismissRequest = { showDropdownMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Theme")
                                        }
                                    },
                                    onClick = {
                                        showThemeDialog = true
                                        showDropdownMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Logout")
                                        }
                                    },
                                    onClick = {
                                        showDropdownMenu = false
                                        userViewModel.logout()
                                    }
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (selectedMenu == 0){
                    FloatingActionButton(
                        onClick = { selectedMenu = 30 },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                BackHandler(enabled = selectedMenu != 0) {
                    selectedMenu = 0
                }

                when (selectedMenu) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Welcome to ClockInGo", color = MaterialTheme.colorScheme.onSurface)
                    }
                    10 -> FindUsersScreen(
                            userViewModel = userViewModel,
                            roleViewModel = roleViewModel,
                            forUpdate = false,
                            onUserSelected = { user -> userViewModel.currentUser(user) }
                        )
                    11 -> CreateUsersScreen(userViewModel = userViewModel, roleViewModel = roleViewModel)
                    12 -> if (selectedUser == null) {
                        FindUsersScreen(
                            userViewModel = userViewModel,
                            roleViewModel = roleViewModel,
                            forUpdate = true,
                            onUserSelected = { user -> userViewModel.currentUser(user) }
                        )
                    } else {
                        UpdateUsersScreen(
                            userViewModel = userViewModel,
                            roleViewModel = roleViewModel,
                            user = selectedUser!!,
                            onFinish = {
                                userViewModel.currentUser(null)
                            }
                        )
                    }
                    20 -> Text("Locations - Find existing screen coming soon")
                    21 -> Text("Locations - Create new screen coming soon")
                    22 -> Text("Locations - Update existing screen coming soon")
                    30 -> Text("Entry's - Add new screen coming soon")
                    31 -> Text("Entry's - Find existing screen coming soon")
                    32 -> Text("Entry's - Update existing screen coming soon")
                    40 -> Text("Exit's - Add new screen coming soon")
                    41 -> Text("Exit's - Find existing screen coming soon")
                    42 -> Text("Exit's - Update existing screen coming soon")
                    43 -> Text("Exit's - Audit existing screen coming soon")
                    else -> Text("Unknown screen")
                }

                if (showThemeDialog) {
                    ShowThemeDialog(
                        currentTheme = selectedTheme,
                        currentMode = selectedMode,
                        onThemeChange = {
                            onThemeChange(it)
                            showThemeDialog = false
                        },
                        onModeChange = {
                            onModeChange(it)
                            showThemeDialog = false
                        },
                        onDismiss = { showThemeDialog = false }
                    )
                }
            }
        }
    }
}