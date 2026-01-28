package com.example.listaimagenes.eltiempo.data.repository

import com.example.listaimagenes.eltiempo.data.local.PreferencesDataSource
import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

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
