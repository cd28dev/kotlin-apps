package com.example.listaimagenes.eltiempo.util

/**
 * Utility object for mapping weather descriptions to emojis.
 * 
 * Provides a visual representation of weather conditions.
 */
object WeatherIconMapper {
    
    /**
     * Maps weather description to appropriate emoji.
     * 
     * @param description Weather description from API
     * @return Emoji representing the weather
     */
    fun getWeatherEmoji(description: String): String {
        return when {
            description.contains("despejado", ignoreCase = true) || 
            description.contains("cielo claro", ignoreCase = true) -> "☀️"
            
            description.contains("nubes", ignoreCase = true) && 
            description.contains("dispersas", ignoreCase = true) -> "⛅"
            
            description.contains("nubes", ignoreCase = true) -> "☁️"
            description.contains("lluvia", ignoreCase = true) -> "🌧️"
            description.contains("tormenta", ignoreCase = true) -> "⛈️"
            description.contains("nieve", ignoreCase = true) -> "❄️"
            description.contains("niebla", ignoreCase = true) || 
            description.contains("bruma", ignoreCase = true) -> "🌫️"
            
            else -> "🌤️"
        }
    }
}
