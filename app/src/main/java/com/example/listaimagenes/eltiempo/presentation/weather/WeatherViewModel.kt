package com.example.listaimagenes.eltiempo.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.domain.usecase.GetCurrentWeatherUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.GetForecastUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.GetLocationUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.UpdateLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Weather Screen.
 * 
 * Manages UI state and business logic for the weather screen.
 * Survives configuration changes (rotation, etc.).
 * 
 * 
 * Benefits of ViewModel:
 * - Survives configuration changes
 * - Lifecycle-aware
 * - Separates UI logic from UI code
 * - Easy to test
 * 
 * @param getCurrentWeatherUseCase Use case for getting current weather
 * @param getForecastUseCase Use case for getting forecast
 * @param getLocationUseCase Use case for getting saved location
 * @param updateLocationUseCase Use case for updating location
 */
class WeatherViewModel constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase
) : ViewModel() {
    
    // Private mutable state
    private val _uiState = MutableStateFlow(WeatherUiState())
    
    // Public immutable state
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    init {
        // Load weather for saved location on init
        loadWeatherForSavedLocation()
    }
    
    /**
     * Load weather for the saved location.
     */
    private fun loadWeatherForSavedLocation() {
        viewModelScope.launch {
            val location = getLocationUseCase()
            loadWeather(location.latitude, location.longitude)
        }
    }
    
    /**
     * Load weather data for given coordinates.
     * 
     * @param lat Latitude
     * @param lon Longitude
     */
    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Fetch weather and forecast concurrently
            val weatherResult = getCurrentWeatherUseCase(lat, lon)
            val forecastResult = getForecastUseCase(lat, lon)
            
            // Update state with results
            _uiState.update {
                it.copy(
                    isLoading = false,
                    weather = weatherResult.getOrNull(),
                    forecast = forecastResult.getOrNull() ?: emptyList(),
                    error = weatherResult.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Update location and reload weather.
     * 
     * @param location New location
     */
    fun updateLocation(location: Location) {
        viewModelScope.launch {
            updateLocationUseCase(location)
            loadWeather(location.latitude, location.longitude)
        }
    }
}
