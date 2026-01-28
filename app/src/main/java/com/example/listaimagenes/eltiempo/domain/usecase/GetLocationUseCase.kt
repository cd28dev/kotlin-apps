package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.repository.LocationRepository

class GetLocationUseCase constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Location {
        return repository.getLocation()
    }
}
