package com.example.listaimagenes.eltiempo.data.remote.dto

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class ForecastResponseDto(
    val list: List<ForecastItemDto>
)

data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>
)

fun ForecastResponseDto.toDomain(): List<Forecast> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    
    return list
        .groupBy { item ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.dt * 1000
            dateFormat.format(calendar.time)
        }
        .values
        .take(4)
        .drop(1)
        .take(3)
        .map { dayForecasts ->
            val forecast = dayForecasts.first()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = forecast.dt * 1000
            
            Forecast(
                date = forecast.dt,
                dayName = dayFormat.format(calendar.time)
                    .replaceFirstChar { it.uppercase() },
                temperature = forecast.main.temp.toInt(),
                minTemperature = (forecast.main.temp - 5).toInt(),
                description = forecast.weather.firstOrNull()?.description ?: "",
                icon = forecast.weather.firstOrNull()?.description ?: ""
            )
        }
}
