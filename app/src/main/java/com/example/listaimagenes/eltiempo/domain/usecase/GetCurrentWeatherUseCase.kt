package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Weather
import com.example.listaimagenes.eltiempo.domain.repository.WeatherRepository

/**
 * Use Case for getting current weather data.
 * 
 * Use Cases encapsulate business logic and make it reusable.
 * They represent a single action the user can perform.
 * 
 * Benefits:
 * - Single Responsibility: One use case, one action
 * - Testability: Easy to unit test business logic
 * - Reusability: Can be used from multiple ViewModels
 * - Clean Code: Business logic separated from UI
 * 
 * @param repository Weather repository for data access
 */
class GetCurrentWeatherUseCase constructor(
    private val repository: WeatherRepository
) {
    /**
     * Execute the use case.
     * 
     * @param lat Latitude
     * @param lon Longitude
     * @return Result containing Weather or error
     */
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> {
        return repository.getCurrentWeather(lat, lon)
    }
}
