package com.example.clockingo.presentation.home.entries

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.clockingo.domain.model.User
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.presentation.viewmodel.EntryViewModel
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.example.clockingo.presentation.viewmodel.UserViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromListWithDelay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FindEntriesScreen(
    entryViewModel: EntryViewModel,
    userViewModel: UserViewModel,
    locationViewModel: LocationViewModel,
    forUpdate: Boolean,
    onEntrySelected: (Entry) -> Unit
) {
    val allEntries by entryViewModel.entryList.collectAsState()
    val allUsers by userViewModel.userList.collectAsState()
    val allLocations by locationViewModel.locationList.collectAsState()

    LaunchedEffect(Unit) {
        entryViewModel.loadEntries()
        userViewModel.loadUsers()
        locationViewModel.loadLocations()
    }

    val parentState = rememberLazyListState()
    val childState = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var entryDataLoader: suspend (Int, Int) -> List<List<String>> = { _, _ -> emptyList() }

    val width: Dp = screenWidth * 0.95f
    val height: Dp = if (isLandscape) {
        screenHeight * 1f
    } else {
        screenHeight * 0.65f
    }
    var showPictureScreen by rememberSaveable { mutableStateOf(false) }
    var imageToShow by rememberSaveable { mutableStateOf("") }

    if (showPictureScreen) {
        PictureScreen(selfieBase64 = imageToShow, onBack = { showPictureScreen = false })
        return
    }

    var searchUserName by remember { mutableStateOf("") }
    var searchLocationAddress by remember { mutableStateOf("") }
    val filteredEntries by remember(allEntries, allUsers, allLocations, searchUserName, searchLocationAddress) {
        derivedStateOf {
            allEntries.filter { entry ->
                val user = allUsers.find { it.id == entry.userId }
                val location = allLocations.find { it.id == entry.locationId }

                (searchUserName.isBlank() || user?.name?.contains(searchUserName.trim(), ignoreCase = true) == true) &&
                        (searchLocationAddress.isBlank() || "${location?.address}, ${location?.city}".contains(searchLocationAddress.trim(), ignoreCase = true))
            }
        }
    }

    LazyColumn(
        state = parentState,
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text("Find Entries", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchUserName, onValueChange = {
                        searchUserName = it;
                    },
                    label = { Text("Find by User Name") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = searchLocationAddress, onValueChange = {
                        searchLocationAddress = it;
                    },
                    label = { Text("Find by Location") }, modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                key(searchUserName, searchLocationAddress, configuration.orientation, entryDataLoader, filteredEntries, allUsers, allLocations){
                    isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    val headers = if (isLandscape) {
                        listOf("ID", "User Name", "Location", "Entry Time")
                    } else {
                        listOf("ID", "User Name", "Location", "Entry Time")
                    }
                    val paginatedEntriesCount = filteredEntries.size

                    val (entryRowMapper, rowDataToEntryMapper) = getEntryRowMappers(isLandscape, allUsers, allLocations)
                    entryDataLoader = dataLoaderFromListWithDelay(
                        sourceProvider = { filteredEntries },
                        rowMapper = entryRowMapper
                    )

                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = entryDataLoader,
                        onEdit = { rowIndex, rowData  -> println("Edit entry at row: $rowIndex") },
                        onDelete = { rowIndex, rowData -> println("Delete entry at row: $rowIndex") },
                        onMoreVert = { rowIndex, rowData ->
                            val selectedRow = rowDataToEntryMapper(rowData)
                            val entry = allEntries.find { it.id == selectedRow.id }
                            entry?.let {
                                imageToShow = it.selfie ?: "No selfie available at row: $rowIndex"
                                showPictureScreen = true
                            }
                        },
                        columnSizeAdaptive = true,
                        columnWidth = 150.dp,
                        editOption = forUpdate,
                        deleteOption = false,
                        horizontalDividers = true,
                        verticalDividers = true,
                        childState = childState,
                        width = width,
                        height = height,
                        totalItems = paginatedEntriesCount
                    )
                }
            }
        }
    }
}

fun getEntryRowMappers(
    isLandscape: Boolean,
    users: List<User>,
    locations: List<Location>
): Pair<(Entry) -> List<String>, (List<String>) -> Entry> {
    val dbFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val displayFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm a", Locale.getDefault())

    val entryToRow: (Entry) -> List<String> = if (isLandscape) {
        { entry ->
            val userName = users.find { it.id == entry.userId }?.name ?: "Unknown User"
            val locationDetail = locations.find { it.id == entry.locationId }
            val locationAddress = if (locationDetail != null) {
                "${locationDetail.address} - ${locationDetail.city}"
            } else {
                "Unknown Location"
            }
            val formattedEntryTime = try {
                val date = dbFormatter.parse(entry.entryTime)
                if (date != null) {
                    displayFormatter.format(date)
                } else {
                    entry.entryTime
                }
            } catch (e: Exception) {
                println("Error parsing entryTime: ${entry.entryTime} - ${e.message}")
                entry.entryTime
            }

            listOf(
                entry.id.toString(),
                userName,
                locationAddress,
                formattedEntryTime
            )
        }
    } else {
        { entry ->
            val userName = users.find { it.id == entry.userId }?.name ?: "Unknown User"
            val locationDetail = locations.find { it.id == entry.locationId }
            val locationAddress = if (locationDetail != null) {
                "${locationDetail.address} - ${locationDetail.city}"
            } else {
                "Unknown Location"
            }
            val formattedEntryTime = try {
                val date = dbFormatter.parse(entry.entryTime)
                if (date != null) {
                    displayFormatter.format(date)
                } else {
                    entry.entryTime
                }
            } catch (e: Exception) {
                println("Error parsing entryTime: ${entry.entryTime} - ${e.message}")
                entry.entryTime
            }

            listOf(
                entry.id.toString(),
                userName,
                locationAddress,
                formattedEntryTime
            )
        }
    }
    val rowToEntry: (List<String>, List<User>, List<Location>) -> Entry = { row, usersList, locationsList ->
        val entryId = row.getOrNull(0)?.toIntOrNull() ?: 0

        val displayedUserName = row.getOrNull(1) ?: ""
        val userId = usersList.find { it.name == displayedUserName }?.id ?: 0

        val displayedLocationDetail = row.getOrNull(2) ?: ""
        val locationId = locationsList.find {
            "${it.address}, ${it.city}" == displayedLocationDetail || it.address == displayedLocationDetail
        }?.id ?: 0
        Entry(
            id = entryId,
            userId = userId,
            locationId = locationId,
            entryTime = row.getOrNull(3) ?: "",
            selfie = null,
            updatedAt = null,
            isSynced = row.getOrNull(4)?.toBooleanStrictOrNull() ?: false,
            deviceId = null
        )
    }
    return Pair(entryToRow) { rowData -> rowToEntry(rowData, users, locations) }
}