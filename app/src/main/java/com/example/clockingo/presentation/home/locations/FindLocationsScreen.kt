package com.example.clockingo.presentation.home.locations

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
import com.example.clockingo.domain.model.Location
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromListWithDelay

@Composable
fun FindLocationsScreen(
    locationViewModel: LocationViewModel,
    forUpdate: Boolean,
    onLocationSelected: (Location) -> Unit,
    onShowQR: (String) -> Unit
) {
    val allLocations by locationViewModel.locationList.collectAsState()
    LaunchedEffect(Unit) {
        locationViewModel.loadLocations()
    }

    val parentState = rememberLazyListState()
    val childState  = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    var isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var searchId by remember { mutableStateOf("") }
    var searchCode by remember { mutableStateOf("") }
    val filteredLocations by remember(allLocations, searchId, searchCode) {
        derivedStateOf {
            allLocations.filter {
                (searchId.isBlank() || it.id.toString().contains(searchId.trim())) &&
                        (searchCode.isBlank() || it.code.contains(searchCode.trim(), ignoreCase = true))
            }
        }
    }

    var locationDataLoader: suspend (Int, Int) -> List<List<String>> = { _, _ -> emptyList() }

    val width: Dp = screenWidth * 0.95f
    val height: Dp = if (isLandscape) {
        screenHeight * 1f
    } else {
        screenHeight * 0.65f
    }
    var showQRScreen by rememberSaveable { mutableStateOf(false) }
    var qrCodeToShow by rememberSaveable { mutableStateOf("") }

    if (showQRScreen) {
        QRScreen(locationCode = qrCodeToShow, onBack = { showQRScreen = false })
        return
    }

    LazyColumn(
        state = parentState,
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text("Find Locations", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchId, onValueChange = {
                        searchId = it
                    },
                    label = { Text("Find by ID") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = searchCode, onValueChange = {
                        searchCode = it
                    },
                    label = { Text("Find by Code") }, modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                key(searchId, searchCode, configuration.orientation, locationDataLoader, filteredLocations){
                    isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    val headers = if (isLandscape) {
                        listOf("ID", "Address", "City", "Company Office")
                    } else {
                        listOf("ID", "Address", "City")
                    }
                    val filteredLocations = allLocations.filter {
                        (searchId.isBlank() || it.id.toString().contains(searchId.trim())) &&
                                (searchCode.isBlank() || it.code.contains(searchCode.trim(), ignoreCase = true))
                    }
                    val paginatedLocationsCount = filteredLocations.size
                    val (locationRowMapper, rowDataToLocationMapper) = getLocationRowMappers(isLandscape)
                    locationDataLoader = dataLoaderFromListWithDelay(
                        sourceProvider = { filteredLocations },
                        rowMapper = locationRowMapper
                    )

                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = locationDataLoader,
                        onEdit = { rowIndex, rowData ->
                            onLocationSelected(rowDataToLocationMapper(rowData))
                            println("Edit location at row: $rowIndex")
                        },
                        onDelete = { rowIndex, rowData -> println("Delete location at row: $rowIndex") },
                        onMoreVert = { rowIndex, _ ->
                            val location = filteredLocations.getOrNull(rowIndex)
                            location?.let {
                                qrCodeToShow = it.code
                                showQRScreen = true
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
                        totalItems = paginatedLocationsCount
                    )
                }
            }
        }
    }
}

fun getLocationRowMappers(
    isLandscape: Boolean
): Pair<(Location) -> List<String>, (List<String>) -> Location> {

    val locationToRow: (Location) -> List<String> = if (isLandscape) {
        { location ->
            listOf(
                location.id.toString(),
                location.address ?: "---",
                location.city ?: "---",
                if (location.isCompanyOffice) "Yes" else "No"
            )
        }
    } else {
        { location ->
            listOf(
                location.id.toString(),
                location.city ?: "---",
                location.address ?: "---"
            )
        }
    }

    val rowToLocation: (List<String>) -> Location = if (isLandscape) {
        { row ->
            Location(
                id = row.getOrNull(0)?.toIntOrNull() ?: 0,
                code = row.getOrNull(1) ?: "",
                address = row.getOrNull(2).takeIf { it != "---" },
                city = row.getOrNull(3).takeIf { it != "---" },
                createdBy = 0,
                isCompanyOffice = row.getOrNull(4) == "Yes"
            )
        }
    } else {
        { row ->
            Location(
                id = row.getOrNull(0)?.toIntOrNull() ?: 0,
                code = row.getOrNull(1) ?: "",
                address = row.getOrNull(2).takeIf { it != "---" },
                city = null,
                createdBy = 0,
                isCompanyOffice = false
            )
        }
    }

    return Pair(locationToRow, rowToLocation)
}