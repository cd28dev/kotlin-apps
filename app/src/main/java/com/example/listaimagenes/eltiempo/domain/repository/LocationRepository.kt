package com.example.listaimagenes.eltiempo.domain.repository

import com.example.listaimagenes.eltiempo.domain.model.Location


interface LocationRepository {

    suspend fun saveLocation(location: Location)
    suspend fun getLocation(): Location
}
