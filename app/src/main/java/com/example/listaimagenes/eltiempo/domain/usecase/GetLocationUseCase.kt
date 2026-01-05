package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

/**
 * Use Case for getting the saved location.
 * 
 * Retrieves the user's last selected location or default.
 */
class GetLocationUseCase constructor(
    private val repository: LocationRepository
) {
    /**
     * Execute the use case.
     * 
     * @return Saved location
     */
    suspend operator fun invoke(): Location {
        return repository.getLocation()
    }
}
