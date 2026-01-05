package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.repository.WeatherRepository

/**
 * Use Case for getting weather forecast.
 * 
 * Fetches the 3-day weather forecast for a given location.
 */
class GetForecastUseCase constructor(
    private val repository: WeatherRepository
) {
    /**
     * Execute the use case.
     * 
     * @param lat Latitude
     * @param lon Longitude
     * @return Result containing list of Forecast or error
     */
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<Forecast>> {
        return repository.getForecast(lat, lon)
    }
}
