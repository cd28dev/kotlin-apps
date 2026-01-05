package com.example.listaimagenes.eltiempo.model

data class WeatherResponse(
    val main: Main,
    val wind: Wind,
    val weather: List<Weather>,
    val sys: Sys,
    val dt: Long,
    val name: String
)
