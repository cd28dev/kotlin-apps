package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

/**
 * Use Case for updating the user's selected location.
 * 
 * Saves the location when user selects a new one from the map.
 */
class UpdateLocationUseCase constructor(
    private val repository: LocationRepository
) {
    /**
     * Execute the use case.
     * 
     * @param location New location to save
     */
    suspend operator fun invoke(location: Location) {
        repository.saveLocation(location)
    }
}
