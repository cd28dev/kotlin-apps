package com.example.listaimagenes.eltiempo.data.repository

import com.example.listaimagenes.eltiempo.data.remote.dto.ForecastResponseDto
import com.example.listaimagenes.eltiempo.data.remote.dto.WeatherResponseDto
import com.example.listaimagenes.eltiempo.data.remote.dto.toDomain
import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Weather
import com.example.listaimagenes.eltiempo.domain.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class WeatherRepositoryImpl : WeatherRepository {

    private val gson = Gson()
    private val apiKey = "bfbfe76d689b86ca6e4d4873b018d111" 
    private val baseUrl = "https://api.openweathermap.org/data/2.5/"

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "${baseUrl}weather?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"
                val json = URL(urlString).readText()
                val dto = gson.fromJson(json, WeatherResponseDto::class.java)
                val domain = dto.toDomain(lat, lon)
                Result.success(domain)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double): Result<List<Forecast>> {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "${baseUrl}forecast?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"
                val json = URL(urlString).readText()
                val dto = gson.fromJson(json, ForecastResponseDto::class.java)
                val domain = dto.toDomain()
                Result.success(domain)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
