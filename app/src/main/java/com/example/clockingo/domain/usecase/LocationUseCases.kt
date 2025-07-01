package com.example.clockingo.domain.usecase

import com.example.clockingo.domain.model.Location
import com.example.clockingo.domain.repository.ILocationRepository
import retrofit2.Response

class GetAllLocationsUseCase(private val repository: ILocationRepository) {
    suspend operator fun invoke(): Response<List<Location>> = repository.getAllLocations()
}

class GetLocationByIdUseCase(private val repository: ILocationRepository) {
    suspend operator fun invoke(id: Int): Response<Location?> = repository.getLocationById(id)
}

class CreateLocationUseCase(private val repository: ILocationRepository) {
    suspend operator fun invoke(location: Location): Response<Unit> {
        val response = repository.createLocation(location)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class UpdateLocationUseCase(private val repository: ILocationRepository) {
    suspend operator fun invoke(location: Location): Response<Unit> {
        val response = repository.updateLocation(location)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

class DeleteLocationUseCase(private val repository: ILocationRepository) {
    suspend operator fun invoke(id: Int): Response<Unit> {
        val response = repository.deleteLocation(id)
        return if (response.isSuccessful) {
            Response.success(Unit)
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}