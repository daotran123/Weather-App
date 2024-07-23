package com.example.weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.weather.ui.weather.WeatherListScreen
import com.example.weather.ui.weather.WeatherNoInternet
import com.example.weather.ui.weather.WeatherScreen
import com.example.weather.utils.WeatherScreenType
import com.example.weather.utils.isNetworkAvailable
import com.google.gson.Gson
import com.plcoding.weatherapp.ui.theme.WeatherTheme
import com.plcoding.weatherapp.ui.weather.WeatherUiState
import com.plcoding.weatherapp.ui.weather.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                val isNetworkAvailable = remember { mutableStateOf(isNetworkAvailable(this)) }
                val selectedCity = remember { mutableStateOf<WeatherUiState?>(null) }
                val weatherList = viewModel.weatherSelectionList

                if (weatherList.isEmpty()){
                    if (isNetworkAvailable.value) {
                        WeatherScreen(
                            cityName = "Vinh Phuc",
                            onBack = {
                                selectedCity.value = null
                                viewModel.setCurrentScreen(WeatherScreenType.List)
                            }
                        )
                    } else {
                        viewModel.getWeatherUiStateFromLocalStorage()
                            ?.let { WeatherNoInternet(
                                uiState = it,
                                onBack = {
                                    selectedCity.value = null
                                    viewModel.setCurrentScreen(WeatherScreenType.List)
                                }
                            ) }
                    }
                }else{
                    if (selectedCity.value == null) {
                        WeatherListScreen(
                            viewModel = viewModel,
                            onCitySelected = { city ->
                                selectedCity.value = city
                            },
//                            onBack = {
//                                selectedCity.value = null
//                                viewModel.setCurrentScreen(WeatherScreenType.List)
//                            }
                        )
                    } else {
                        val cachedWeatherUiState = viewModel.getWeatherListFromSharedPreferences().find { it.weather?.name == selectedCity.value!!.weather?.name }
                        if (isNetworkAvailable.value || cachedWeatherUiState == null) {
                            WeatherScreen(
                                cityName = selectedCity.value!!.weather?.name.orEmpty(),
                                onBack = {
                                    selectedCity.value = null
                                    viewModel.setCurrentScreen(WeatherScreenType.List)
                                }
                            )
                        } else {
                            WeatherNoInternet(
                                uiState = cachedWeatherUiState,
                                onBack = {
                                    selectedCity.value = null
                                    viewModel.setCurrentScreen(WeatherScreenType.List)
                                }
                            )
                        }
                    }
                }



                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        isNetworkAvailable.value = true
                    }

                    override fun onLost(network: Network) {
                        isNetworkAvailable.value = false
                    }
                }

                val networkRequest = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

                DisposableEffect(key1 = Unit) {
                    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                    onDispose {
                        connectivityManager.unregisterNetworkCallback(networkCallback)
                    }
                }
            }
        }
    }

    private fun getCachedWeatherUiStateFromSharedPreferences(cityName: String): WeatherUiState? {
        val sharedPreferences = getSharedPreferences("WeatherData", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("cachedWeatherUiState_$cityName", null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, WeatherUiState::class.java)
        } else {
            null
        }
    }
}
