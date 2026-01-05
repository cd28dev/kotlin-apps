package com.example.listaimagenes.eltiempo.presentation.weather

import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Weather

/**
 * UI State for the Weather Screen.
 * 
 * This data class represents the complete state of the UI.
 * It follows the Single Source of Truth principle.
 * 
 * Benefits:
 * - Immutability: State is read-only, changes create new instances
 * - Predictability: UI always reflects this state
 * - Testability: Easy to test different states
 * - Time-travel debugging: Can replay states
 * 
 * @param isLoading Whether data is being loaded
 * @param weather Current weather data
 * @param forecast List of forecast for next days
 * @param error Error message if any
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val forecast: List<Forecast> = emptyList(),
    val error: String? = null
)
