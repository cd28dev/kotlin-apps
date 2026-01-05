package com.example.listaimagenes.eltiempo.domain.model

/**
 * Domain model representing weather data.
 * This is the core business model, independent of any framework or library.
 */
data class Weather(
    val temperature: Int,
    val description: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val date: Long,
    val location: Location
)

/**
 * Domain model representing a location.
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
)

/**
 * Domain model representing a weather forecast for a specific day.
 */
data class Forecast(
    val date: Long,
    val dayName: String,
    val temperature: Int,
    val minTemperature: Int,
    val description: String,
    val icon: String
)
