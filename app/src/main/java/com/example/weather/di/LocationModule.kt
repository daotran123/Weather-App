package com.example.weather.di

import android.app.Application
import com.example.weather.data.location.DefaultLocationRepository
import com.example.weather.data.location.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(application)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application
    ): LocationRepository{
        return DefaultLocationRepository(fusedLocationProviderClient, application)
    }
}