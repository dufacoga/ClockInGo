package com.example.clockingo.presentation.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.presentation.home.entries.FindEntriesScreen
import com.example.clockingo.presentation.home.entries.ScanScreen
import com.example.clockingo.presentation.home.entries.SelfieScreen
import com.example.clockingo.presentation.home.locations.CreateLocationsScreen
import com.example.clockingo.presentation.home.locations.FindLocationsScreen
import com.example.clockingo.presentation.home.locations.QRScreen
import com.example.clockingo.presentation.home.locations.UpdateLocationsScreen
import com.example.clockingo.presentation.home.users.CreateUsersScreen
import com.example.clockingo.presentation.home.users.FindUsersScreen
import com.example.clockingo.presentation.home.users.UpdateUsersScreen
import com.example.clockingo.presentation.utils.VibrateDevice
import com.example.clockingo.presentation.viewmodel.EntryViewModel
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.example.clockingo.presentation.viewmodel.RoleViewModel
import com.example.clockingo.presentation.viewmodel.UserViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.clockingo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onThemeChange: (ClockInGoThemeOption) -> Unit,
    onModeChange: (ThemeMode) -> Unit,
    selectedTheme: ClockInGoThemeOption,
    selectedMode: ThemeMode,
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    locationViewModel: LocationViewModel,
    entryViewModel: EntryViewModel,
    isOnline: Boolean
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userSaver: Saver<User?, Any> = mapSaver(
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

    val locationSaver: Saver<Location?, Any> = mapSaver(
        save = { location ->
            if (location == null) emptyMap()
            else mapOf(
                "id" to location.id,
                "code" to location.code,
                "address" to location.address,
                "city" to location.city,
                "createdBy" to location.createdBy,
                "isCompanyOffice" to location.isCompanyOffice
            )
        },
        restore = {
            if (it.isEmpty()) null
            else Location(
                id = it["id"] as Int,
                code = it["code"] as String,
                address = it["address"] as String?,
                city = it["city"] as String?,
                createdBy = it["createdBy"] as Int,
                isCompanyOffice = it["isCompanyOffice"] as Boolean
            )
        }
    )

    val entrySaver: Saver<Entry?, Any> = mapSaver(
        save = { entry ->
            if (entry == null) emptyMap()
            else mapOf(
                "id" to entry.id,
                "userId" to entry.userId,
                "locationId" to entry.locationId,
                "entryTime" to entry.entryTime,
                "selfie" to entry.selfie,
                "updatedAt" to entry.updatedAt,
                "isSynced" to entry.isSynced,
                "deviceId" to entry.deviceId
            )
        },
        restore = {
            if (it.isEmpty()) null
            else Entry(
                id = it["id"] as Int,
                userId = it["userId"] as Int,
                locationId = it["locationId"] as Int,
                entryTime = it["entryTime"] as String,
                selfie = it["selfie"] as String,
                updatedAt = it["updatedAt"] as String,
                isSynced = it["isSynced"] as Boolean,
                deviceId = it["deviceId"] as String
            )
        }
    )

    var selectedMenu by rememberSaveable { mutableIntStateOf(0) }
    var selectedUser: User? by rememberSaveable(stateSaver = userSaver) {
        mutableStateOf(null)
    }
    var selectedLocation: Location? by rememberSaveable(stateSaver = locationSaver) {
        mutableStateOf(null)
    }
    var selectedEntry: Entry? by rememberSaveable(stateSaver = entrySaver) {
        mutableStateOf(null)
    }
    var scannedLocationId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val expandedItems = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberScrollState()

    val currentUser by userViewModel.currentUser.collectAsState()
    val currentLoggedInUserId by remember { mutableIntStateOf(1) }
    var qrCodeToShow by rememberSaveable { mutableStateOf<String?>(null) }
    val currentContext = LocalContext.current

    LaunchedEffect(currentUser) {
        selectedUser = currentUser
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var menuItems = listOf<DrawerMenuItem>()
    when (currentUser?.roleId) {
        1 -> {
            menuItems = listOf(
                DrawerMenuItem(0,stringResource(R.string.home_drawer_home)),
                DrawerMenuItem(
                    1,
                    stringResource(R.string.home_drawer_users),
                    subItems = listOf(
                        DrawerSubItems(10, stringResource(R.string.home_drawer_find_existing), true),
                        DrawerSubItems(11, stringResource(R.string.home_drawer_create_new), true),
                        DrawerSubItems(12, stringResource(R.string.home_drawer_update_existing), true)
                    ),
                    true
                ),
                DrawerMenuItem(
                    2,
                    stringResource(R.string.home_drawer_locations),
                    subItems = listOf(
                        DrawerSubItems(20, stringResource(R.string.home_drawer_find_existing), true),
                        DrawerSubItems(21, stringResource(R.string.home_drawer_create_new), true),
                        DrawerSubItems(22, stringResource(R.string.home_drawer_update_existing), true)
                    ),
                    true
                ),
                DrawerMenuItem(
                    3,
                    stringResource(R.string.home_drawer_entries),
                    subItems = listOf(
                        DrawerSubItems(30, stringResource(R.string.home_drawer_add_new), true),
                        DrawerSubItems(31, stringResource(R.string.home_drawer_find_existing), true)
                    ),
                    true
                ),
                DrawerMenuItem(
                    4,
                    stringResource(R.string.home_drawer_exits),
                    subItems = listOf(
                        DrawerSubItems(40, stringResource(R.string.home_drawer_add_new), true),
                        DrawerSubItems(41, stringResource(R.string.home_drawer_find_existing), true),
                        DrawerSubItems(42, stringResource(R.string.home_drawer_update_existing), true),
                        DrawerSubItems(43, stringResource(R.string.home_drawer_audit_existing), true)
                    ),
                    true
                )
            )
        }
        2 -> {
            menuItems = listOf(
                DrawerMenuItem(0,stringResource(R.string.home_drawer_home)),
                DrawerMenuItem(
                    3,
                    stringResource(R.string.home_drawer_entries),
                    subItems = listOf(
                        DrawerSubItems(30, stringResource(R.string.home_drawer_add_new), true),
                        DrawerSubItems(31, stringResource(R.string.home_drawer_find_existing), true)
                    ),
                    true
                )
            )
        }
        else -> {
            menuItems = listOf(
                DrawerMenuItem(0,stringResource(R.string.home_drawer_home))
            )
        }
    }

    val currentLocation by locationViewModel.currentLocation.collectAsState()
    LaunchedEffect(currentLocation) {
        selectedLocation = currentLocation
    }

    var allowScan by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(selectedMenu) {
        if (selectedMenu == 30) {
            allowScan = !entryViewModel.hasCheckedInRecently(currentLoggedInUserId)
        }
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
                        val isItemEnabled = isOnline || item.id == 0 || item.subItems.any { it.id == 30 || it.id == 40 }
                        NavigationDrawerItem(
                            label = { Text(item.title) },
                            selected = selectedMenu == item.id,
                            onClick = {
                                if (isItemEnabled) {
                                    if (item.subItems.isEmpty()) {
                                        selectedMenu = item.id
                                        scope.launch { drawerState.close() }
                                    } else {
                                        expandedItems[item.id] = !(expandedItems[item.id] ?: false)
                                    }
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
                                        if (isItemEnabled) {
                                            selectedMenu = sub.id
                                            scope.launch { drawerState.close() }
                                        }
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
                    title = { Text(stringResource(R.string.home_app_title), color = MaterialTheme.colorScheme.onPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.home_app_menu_description), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showDropdownMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.home_app_more_description), tint = MaterialTheme.colorScheme.onPrimary)
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
                                            Text(stringResource(R.string.home_app_theme))
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
                                            Text(stringResource(R.string.home_app_logout))
                                        }
                                    },
                                    onClick = {
                                        showDropdownMenu = false
                                        userViewModel.logout()
                                    },
                                    enabled = isOnline
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
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_drawer_add_new))
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
                    selectedUser = null
                    selectedLocation = null
                    selectedEntry = null
                    scannedLocationId = null
                }

                when (selectedMenu) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        val greeting = stringResource(R.string.home_welcome_title)
                        val currentUserName = currentUser?.name ?: ""
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$greeting $currentUserName",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.home_welcome_message),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                    20 -> if (qrCodeToShow != null) {
                        QRScreen(
                            locationCode = qrCodeToShow!!,
                            onBack = { qrCodeToShow = null }
                        )
                    } else {
                        FindLocationsScreen(
                            locationViewModel = locationViewModel,
                            forUpdate = false,
                            onLocationSelected = { location ->
                                locationViewModel.currentLocation(location)
                            },
                            onShowQR = { code ->
                                qrCodeToShow = code
                            }
                        )
                    }
                    21 -> CreateLocationsScreen(locationViewModel = locationViewModel, currentUserId = currentLoggedInUserId)
                    22 -> if (selectedLocation == null) {
                        FindLocationsScreen(
                            locationViewModel = locationViewModel,
                            forUpdate = true,
                            onLocationSelected = { location -> locationViewModel.currentLocation(location) },
                            onShowQR = {  }
                        )
                    } else {
                        UpdateLocationsScreen(
                            locationViewModel = locationViewModel,
                            location = selectedLocation!!,
                            onFinish = {
                                locationViewModel.currentLocation(null)
                                selectedLocation = null
                            }
                        )
                    }
                    30 -> {
                        when (allowScan) {
                            null -> Text(stringResource(R.string.home_checking_entry))

                            false -> {
                                Toast.makeText(currentContext, stringResource(R.string.home_already_checked_in), Toast.LENGTH_LONG).show()
                                allowScan = null
                                VibrateDevice.vibrate(currentContext)
                                selectedMenu = 0
                            }

                            true -> {
                                if (scannedLocationId == null) {
                                    ScanScreen(
                                        locationViewModel = locationViewModel,
                                        onQrScanned = { id -> scannedLocationId = id },
                                        onError = { msg ->
                                            Toast.makeText(
                                                currentContext,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                } else {
                                    SelfieScreen(
                                        entryViewModel = entryViewModel,
                                        currentUserId = currentLoggedInUserId,
                                        currentLocationId = scannedLocationId!!,
                                        onQrScanned = {
                                            scannedLocationId = null
                                            allowScan = null
                                            selectedMenu = 0
                                        },
                                        onClearLocation = { scannedLocationId = null },
                                        onError = { msg ->
                                            Toast.makeText(
                                                currentContext,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        isOnline = isOnline
                                    )
                                }
                            }
                        }
                    }
                    31 -> FindEntriesScreen(
                        entryViewModel = entryViewModel,
                        userViewModel = userViewModel,
                        locationViewModel = locationViewModel,
                        forUpdate = false,
                        currentUser = currentUser!!
                    )
                    40 -> Text(stringResource(R.string.home_exit_add_coming_soon))
                    41 -> Text(stringResource(R.string.home_exit_find_coming_soon))
                    42 -> Text(stringResource(R.string.home_exit_update_coming_soon))
                    43 -> Text(stringResource(R.string.home_exit_audit_coming_soon))
                    else -> Text(stringResource(R.string.home_unknown_screen))
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