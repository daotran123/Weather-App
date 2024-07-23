package com.plcoding.weatherapp.data.repository

import com.plcoding.weatherapp.model.Weather
import com.plcoding.weatherapp.utils.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(city: String): Flow<Result<Weather>>
}