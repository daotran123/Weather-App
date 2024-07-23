package com.plcoding.weatherapp.di

import com.google.gson.GsonBuilder
import com.plcoding.weatherapp.data.network.WeatherAPI
import com.plcoding.weatherapp.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    val isDebugMode: Boolean = true

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLogginfInterceptor: HttpLoggingInterceptor): OkHttpClient{
        val okHttpClient = OkHttpClient.Builder()
        return if (isDebugMode){
            okHttpClient.addInterceptor(httpLogginfInterceptor).build()
        } else{
            okHttpClient.build()
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherAPI = retrofit
        .create(WeatherAPI::class.java)
}