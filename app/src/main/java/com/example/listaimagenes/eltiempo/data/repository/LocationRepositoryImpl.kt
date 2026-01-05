package com.example.listaimagenes.eltiempo.data.repository

import com.example.listaimagenes.eltiempo.data.local.PreferencesDataSource
import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

/**
 * Implementation of LocationRepository.
 * 
 * Manages user's location using local storage.
 * 
 * @param preferencesDataSource Local data source injected by Hilt
 */
class LocationRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : LocationRepository {
    
    override suspend fun saveLocation(location: Location) {
        preferencesDataSource.saveLocation(location)
    }
    
    override suspend fun getLocation(): Location {
        return preferencesDataSource.getLocation()
    }
}
