package com.plcoding.weatherapp.data.network

import com.example.weather.BuildConfig
import com.plcoding.weatherapp.data.model.ForecastResponse
import com.plcoding.weatherapp.utils.DEFAULT_WEATHER_DESTINATION
import com.plcoding.weatherapp.utils.NUMBER_OF_DAYS
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("q") city: String = DEFAULT_WEATHER_DESTINATION,
        @Query("days") days: Int = NUMBER_OF_DAYS,
    ): ForecastResponse
}