package com.example.listaimagenes.eltiempo.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.listaimagenes.eltiempo.domain.model.Forecast
import com.example.listaimagenes.eltiempo.domain.model.Weather
import com.example.listaimagenes.eltiempo.util.WeatherIconMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main Weather Screen composable - Diseño original mejorado
 */
@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onRefresh: (Double, Double) -> Unit,
    onNavigateToMap: () -> Unit = {}
) {
    WeatherContent(
        uiState = state,
        onMapClick = onNavigateToMap
    )
}

@Composable
private fun WeatherContent(
    uiState: WeatherUiState,
    onMapClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        WeatherHeader(onMapClick = onMapClick)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Loading or Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.weather != null -> {
                WeatherCard(weather = uiState.weather)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState.forecast.isNotEmpty()) {
                    ForecastSection(forecast = uiState.forecast)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Location info
                LocationSection(weather = uiState.weather)
            }
        }
    }
}

@Composable
private fun WeatherHeader(onMapClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "El Tiempo",
            fontSize = 26.sp,
            color = Color(0xFF1976D2),
            fontWeight = FontWeight.Bold
        )
        
        Button(
            onClick = onMapClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            )
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Mapa",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("MAPA", color = Color.White)
        }
    }
}

@Composable
private fun WeatherCard(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Date
            val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("es", "ES"))
            Text(
                text = dateFormat.format(Date(weather.date * 1000)),
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Temperature label
            Text(
                text = "Temperatura",
                fontSize = 16.sp,
                color = Color(0xFF424242)
            )
            
            // Temperature value
            Text(
                text = "${weather.temperature}°C",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            
            // Description
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                color = Color(0xFF616161),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Weather Info Grid with large emojis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem(
                    icon = "☀️",
                    label = "Amanecer",
                    value = formatTime(weather.sunrise)
                )
                WeatherInfoItem(
                    icon = "💨",
                    label = "Viento",
                    value = "${weather.windSpeed} m/s"
                )
                WeatherInfoItem(
                    icon = "🌅",
                    label = "Atardecer",
                    value = formatTime(weather.sunset)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Humidity and Pressure
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💧 ", fontSize = 20.sp)
                    Text(
                        text = "Humedad: ${weather.humidity}%",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌡️ ", fontSize = 20.sp)
                    Text(
                        text = "Presión: ${weather.pressure} hPa",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherInfoItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
    }
}

@Composable
private fun ForecastSection(forecast: List<Forecast>) {
    Text(
        text = "Pronóstico para los siguientes días",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1976D2)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        forecast.forEach { day ->
            ForecastCard(
                forecast = day,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ForecastCard(
    forecast: Forecast,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = forecast.dayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = WeatherIconMapper.getWeatherEmoji(forecast.description),
                fontSize = 36.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${forecast.temperature}°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
            
            Text(
                text = "${forecast.minTemperature}°",
                fontSize = 14.sp,
                color = Color(0xFF1976D2)
            )
        }
    }
}

@Composable
private fun LocationSection(weather: Weather) {
    Text(
        text = "Ubicación",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1976D2)
    )
    
    Spacer(modifier = Modifier.height(4.dp))
    
    Text(
        text = "Latitud: ${weather.location.latitude}",
        fontSize = 14.sp,
        color = Color(0xFF424242)
    )
    
    Text(
        text = "Longitud: ${weather.location.longitude}",
        fontSize = 14.sp,
        color = Color(0xFF424242)
    )
}

private fun formatTime(timestamp: Long): String {
    val format = SimpleDateFormat("hh:mm a. m.", Locale("es", "ES"))
    return format.format(Date(timestamp * 1000))
}
