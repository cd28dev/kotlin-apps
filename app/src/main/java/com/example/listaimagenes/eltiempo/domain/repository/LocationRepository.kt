package com.example.listaimagenes.eltiempo.domain.repository

import com.example.listaimagenes.eltiempo.domain.model.Location

/**
 * Repository interface for location data operations.
 * 
 * Handles saving and retrieving user's selected location.
 */
interface LocationRepository {
    
    /**
     * Save the current location.
     * 
     * @param location Location to save
     */
    suspend fun saveLocation(location: Location)
    
    /**
     * Get the saved location.
     * 
     * @return Saved location or default if none exists
     */
    suspend fun getLocation(): Location
}
