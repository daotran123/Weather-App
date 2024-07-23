package com.plcoding.weatherapp.di

import com.plcoding.weatherapp.data.network.WeatherAPI
import com.plcoding.weatherapp.data.repository.DefaultWeatherRepository
import com.plcoding.weatherapp.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(weatherAPI: WeatherAPI): WeatherRepository =
        DefaultWeatherRepository(weatherAPI)
}
