package com.example.listaimagenes.eltiempo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.eltiempo.domain.model.Location
import com.example.listaimagenes.eltiempo.presentation.weather.WeatherScreen
import com.example.listaimagenes.eltiempo.presentation.weather.WeatherViewModel
import com.example.listaimagenes.eltiempo.presentation.weather.WeatherViewModelFactory

@Composable
fun ElTiempoScreen() {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(context)
    )
    
    val state = viewModel.uiState.collectAsState().value
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lat = result.data?.getDoubleExtra("SELECTED_LAT", 0.0)
            val lon = result.data?.getDoubleExtra("SELECTED_LON", 0.0)
            if (lat != null && lon != null && (lat != 0.0 || lon != 0.0)) {
                 viewModel.updateLocation(Location(lat, lon, "Ubicación seleccionada"))
            }
        }
    }
    
    WeatherScreen(
        state = state,
        onRefresh = { lat, lon -> viewModel.loadWeather(lat, lon) },
        onNavigateToMap = {
            android.widget.Toast.makeText(context, "Abriendo mapa...", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MapActivity::class.java).apply {
                state.weather?.let {
                    putExtra("CURRENT_LAT", it.location.latitude)
                    putExtra("CURRENT_LON", it.location.longitude)
                }
            }
            launcher.launch(intent)
        }
    )
}
