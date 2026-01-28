package com.example.listaimagenes.eltiempo.data.local

import android.content.Context
import com.example.listaimagenes.eltiempo.domain.model.Location

class PreferencesDataSource(
    private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLocation(location: Location) {
        prefs.edit().apply {
            putString(KEY_LATITUDE, location.latitude.toString())
            putString(KEY_LONGITUDE, location.longitude.toString())
            putString(KEY_NAME, location.name)
            apply()
        }
    }

    fun getLocation(): Location {
        val lat = prefs.getString(KEY_LATITUDE, DEFAULT_LAT)?.toDoubleOrNull() ?: -5.1945
        val lon = prefs.getString(KEY_LONGITUDE, DEFAULT_LON)?.toDoubleOrNull() ?: -80.6328
        val name = prefs.getString(KEY_NAME, "") ?: ""
        return Location(lat, lon, name)
    }
    
    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_NAME = "name"
        private const val DEFAULT_LAT = "-5.1945"
        private const val DEFAULT_LON = "-80.6328"
    }
}
