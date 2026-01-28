package com.example.listaimagenes.eltiempo.domain.model

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


data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
)


data class Forecast(
    val date: Long,
    val dayName: String,
    val temperature: Int,
    val minTemperature: Int,
    val description: String,
    val icon: String
)
