package com.example.listaimagenes.eltiempo.domain.repository

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Weather
interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>
    suspend fun getForecast(lat: Double, lon: Double): Result<List<Forecast>>
}
