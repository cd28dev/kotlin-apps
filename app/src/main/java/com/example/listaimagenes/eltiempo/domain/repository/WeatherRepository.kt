package com.example.listaimagenes.eltiempo.domain.repository

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Weather

/**
 * Repository interface for weather data operations.
 * 
 * This interface defines the contract for weather data access.
 * It follows the Repository Pattern, abstracting the data source
 * from the business logic.
 * 
 * Benefits:
 * - Testability: Easy to mock for unit tests
 * - Flexibility: Can swap implementations (API, local DB, cache)
 * - Clean Architecture: Domain layer doesn't depend on data layer
 */
interface WeatherRepository {
    
    /**
     * Get current weather for a specific location.
     * 
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Result containing Weather data or error
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>
    
    /**
     * Get weather forecast for the next days.
     * 
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Result containing list of Forecast or error
     */
    suspend fun getForecast(lat: Double, lon: Double): Result<List<Forecast>>
}
