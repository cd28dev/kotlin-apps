package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.repository.WeatherRepository

class GetForecastUseCase constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<Forecast>> {
        return repository.getForecast(lat, lon)
    }
}
