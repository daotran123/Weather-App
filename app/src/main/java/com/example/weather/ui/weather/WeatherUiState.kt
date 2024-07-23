package com.plcoding.weatherapp.ui.weather

import com.plcoding.weatherapp.model.Weather

data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)
