package com.plcoding.weatherapp.data.repository

import com.plcoding.weatherapp.data.model.toWeather
import com.plcoding.weatherapp.data.network.WeatherAPI
import com.plcoding.weatherapp.model.Weather
import com.plcoding.weatherapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherAPI: WeatherAPI,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {
    override fun getWeatherForecast(city: String): Flow<Result<Weather>> = flow {
        emit(Result.Loading)
        try {
            val result = weatherAPI.getWeatherForecast(city = city).toWeather()
            emit(Result.Success(result))
        } catch (exception: HttpException){
            emit(Result.Error(exception.message.orEmpty()))
        } catch (exception: IOException){
            emit(Result.Error("Please check your network connection and try again!"))
        }
    }.flowOn(dispatcher)

}