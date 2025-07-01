package com.example.clockingo.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onThemeChange: (ClockInGoThemeOption) -> Unit,
    onModeChange: (ThemeMode) -> Unit,
    selectedTheme: ClockInGoThemeOption,
    selectedMode: ThemeMode
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
                DrawerSubItems(12, "Update existing", true),
                DrawerSubItems(13, "Delete existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            2,
            "Locations",
            subItems = listOf(
                DrawerSubItems(20, "Find existing", true),
                DrawerSubItems(21, "Create new", true),
                DrawerSubItems(22, "Update existing", true),
                DrawerSubItems(23, "Delete existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            3,
            "Entry's",
            subItems = listOf(
                DrawerSubItems(30, "Add new", true),
                DrawerSubItems(31, "Find existing", true),
                DrawerSubItems(32, "Update existing", true)
            ),
            true
        ),
        DrawerMenuItem(
            4,
            "Exit's",
            subItems = listOf(
                DrawerSubItems(40, "Add new", true),
                DrawerSubItems(41, "Find existing", true),
                DrawerSubItems(42, "Update existing", true),
                DrawerSubItems(43, "Audit existing", true)
            ),
            true
        )
    )
    var selectedMenu by remember { mutableIntStateOf(0) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val expandedItems = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberScrollState()

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
                                        onLogout()
                                    }
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { selectedMenu = 30 },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
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
                when (selectedMenu) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Welcome to ClockInGo", color = MaterialTheme.colorScheme.onSurface)
                    }
                    10 -> Text("Users - Find existing screen coming soon")
                    11 -> Text("Users - Create new screen coming soon")
                    12 -> Text("Users - Update existing screen coming soon")
                    13 -> Text("Users - Delete existing screen coming soon")
                    20 -> Text("Locations - Find existing screen coming soon")
                    21 -> Text("Locations - Create new screen coming soon")
                    22 -> Text("Locations - Update existing screen coming soon")
                    23 -> Text("Locations - Delete existing screen coming soon")
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