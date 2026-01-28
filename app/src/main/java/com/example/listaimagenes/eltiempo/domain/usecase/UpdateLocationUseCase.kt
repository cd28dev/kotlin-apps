package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

class UpdateLocationUseCase constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location) {
        repository.saveLocation(location)
    }
}
