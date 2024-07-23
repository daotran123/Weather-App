package com.example.weather.data.location

import android.location.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?
}