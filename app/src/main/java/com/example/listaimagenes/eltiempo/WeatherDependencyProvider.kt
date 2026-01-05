package com.example.listaimagenes.eltiempo

import android.content.Context
import com.example.listaimagenes.eltiempo.data.local.PreferencesDataSource
import com.example.listaimagenes.eltiempo.data.repository.LocationRepositoryImpl
import com.example.listaimagenes.eltiempo.data.repository.WeatherRepositoryImpl
import com.example.listaimagenes.eltiempo.domain.usecase.GetCurrentWeatherUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.GetForecastUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.GetLocationUseCase
import com.example.listaimagenes.eltiempo.domain.usecase.UpdateLocationUseCase
import com.example.listaimagenes.eltiempo.presentation.weather.WeatherViewModel

object WeatherDependencyProvider {

    private var viewModel: WeatherViewModel? = null

    fun provideWeatherViewModel(context: Context): WeatherViewModel {
        if (viewModel == null) {
            val preferencesDataSource = PreferencesDataSource(context)

            val weatherRepository = WeatherRepositoryImpl()
            val locationRepository = LocationRepositoryImpl(preferencesDataSource)

            val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository)
            val getForecastUseCase = GetForecastUseCase(weatherRepository)
            val getLocationUseCase = GetLocationUseCase(locationRepository)
            val updateLocationUseCase = UpdateLocationUseCase(locationRepository)
            
            // Note: We might need to adjust this depending on the ViewModel constructor
            viewModel = WeatherViewModel(
                getCurrentWeatherUseCase,
                getForecastUseCase,
                getLocationUseCase,
                updateLocationUseCase
            )
        }
        return viewModel!!
    }
}
