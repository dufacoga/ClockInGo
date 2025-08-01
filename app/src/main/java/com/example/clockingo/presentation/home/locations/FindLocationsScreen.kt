package com.example.clockingo.presentation.home.locations

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.clockingo.domain.model.Location
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.example.materialdatatable.MaterialDataTableC
import com.example.materialdatatable.dataLoaderFromList
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@Composable
fun FindLocationsScreen(
    locationViewModel: LocationViewModel,
    forUpdate: Boolean,
    forMore: Boolean,
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

    var searchAddress by remember { mutableStateOf("") }
    var searchCity by remember { mutableStateOf("") }
    val filteredLocations by remember(allLocations, searchAddress, searchCity) {
        derivedStateOf {
            allLocations.filter {
                (searchAddress.isBlank() || it.address.toString().contains(searchAddress.trim())) &&
                        (searchCity.isBlank() || it.city.toString().contains(searchCity.trim()))
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
            Text(stringResource(R.string.find_location_title), style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchAddress, onValueChange = {
                        searchAddress = it
                    },
                    label = { Text(stringResource(R.string.find_location_search_address)) }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = searchCity, onValueChange = {
                        searchCity = it
                    },
                    label = { Text(stringResource(R.string.find_location_search_city)) }, modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                key(searchAddress, searchCity, configuration.orientation, locationDataLoader, filteredLocations){
                    isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    val headers = if (isLandscape) {
                        listOf(
                            stringResource(R.string.find_location_header_id),
                            stringResource(R.string.find_location_header_address),
                            stringResource(R.string.find_location_header_city),
                            stringResource(R.string.find_location_header_company_office)
                        )
                    } else {
                        listOf(
                            stringResource(R.string.find_location_header_id),
                            stringResource(R.string.find_location_header_address),
                            stringResource(R.string.find_location_header_city)
                        )
                    }
                    val paginatedLocationsCount = filteredLocations.size
                    val locationDataLoader = dataLoaderFromList(
                        sourceProvider = { filteredLocations }
                    )

                    val placeholder = LocalContext.current.getString(R.string.find_location_value_placeholder)
                    val yesLabel = LocalContext.current.getString(R.string.find_location_value_yes)
                    val noLabel = LocalContext.current.getString(R.string.find_location_value_no)

                    val locationToRow: (Location) -> List<String> = if (isLandscape) {
                        { location ->
                            listOf(
                                location.id.toString(),
                                location.address ?: placeholder,
                                location.city ?: placeholder,
                                if (location.isCompanyOffice) yesLabel else noLabel
                            )
                        }
                    } else {
                        { location ->
                            listOf(
                                location.id.toString(),
                                location.address ?: placeholder,
                                location.city ?: placeholder
                            )
                        }
                    }

                    MaterialDataTableC(
                        headers = headers,
                        dataLoader = locationDataLoader,
                        rowMapper = locationToRow,
                        onEdit = { location ->
                            onLocationSelected(location)
                            println("Edit location at row: ${location.id}")
                        },
                        onDelete = { location -> println("Delete location at row: ${location.id}") },
                        onMore = { location ->
                            qrCodeToShow = location.code
                            showQRScreen = true
                        },
                        columnSizeAdaptive = true,
                        columnWidth = 150.dp,
                        moreOption = forMore,
                        editOption = forUpdate,
                        deleteOption = false,
                        horizontalDividers = true,
                        verticalDividers = true,
                        paginationRowFixed = true,
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