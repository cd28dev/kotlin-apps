package com.example.listaimagenes.eltiempo.domain.usecase

import com.example.listaimagenes.eltiempo.domain.model.Weather
import com.example.listaimagenes.eltiempo.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase constructor(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> {
        return repository.getCurrentWeather(lat, lon)
    }
}
