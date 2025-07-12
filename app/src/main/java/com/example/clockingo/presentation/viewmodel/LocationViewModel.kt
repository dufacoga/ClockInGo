package com.example.clockingo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val getLocationByCodeUseCase: GetLocationByCodeUseCase,
    private val createLocationUseCase: CreateLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _locationList = MutableStateFlow<List<Location>>(emptyList())
    val locationList: StateFlow<List<Location>> get() = _locationList

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> get() = _currentLocation

    fun currentLocation(location: Location?) {
        _currentLocation.value = location
    }

    fun loadLocations() {
        viewModelScope.launch {
            val response = getAllLocationsUseCase()
            if (response.isSuccessful) {
                _locationList.value = response.body() ?: emptyList()
            }
        }
    }

    fun getLocationById(id: Int) {
        viewModelScope.launch {
            val response = getLocationByIdUseCase(id)
            if (response.isSuccessful) {
                _currentLocation.value = response.body()
            }
        }
    }

    fun getLocationByCode(code: String) {
        viewModelScope.launch {
            val response = getLocationByCodeUseCase(code)
            if (response.isSuccessful) {
                _currentLocation.value = response.body()
            }
        }
    }

    fun createLocation(location: Location, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = createLocationUseCase(location)
            onResult(response.isSuccessful)
        }
    }

    fun updateLocation(location: Location, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = updateLocationUseCase(location)
            onResult(response.isSuccessful)
        }
    }

    fun deleteLocation(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = deleteLocationUseCase(id)
            onResult(response.isSuccessful)
        }
    }
}