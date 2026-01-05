package com.example.listaimagenes.eltiempo.data.remote.dto

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.model.Weather

/**
 * Data Transfer Object (DTO) for Weather API response.
 */
data class WeatherResponseDto(
    val main: MainDto,
    val wind: WindDto,
    val weather: List<WeatherDescriptionDto>,
    val sys: SysDto,
    val dt: Long,
    val name: String
)

data class MainDto(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class WindDto(
    val speed: Double
)

data class WeatherDescriptionDto(
    val description: String
)

data class SysDto(
    val sunrise: Long,
    val sunset: Long
)

/**
 * Extension function to convert DTO to Domain Model.
 */
fun WeatherResponseDto.toDomain(lat: Double, lon: Double): Weather {
    return Weather(
        temperature = main.temp.toInt(),
        description = weather.firstOrNull()?.description ?: "",
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        date = dt,
        location = Location(lat, lon, name)
    )
}
