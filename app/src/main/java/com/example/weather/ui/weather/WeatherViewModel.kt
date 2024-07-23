package com.plcoding.weatherapp.ui.weather

import android.content.Context
import android.service.autofill.FillEventHistory
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.location.LocationRepository
import com.example.weather.utils.WeatherScreenType
import com.google.gson.Gson
import com.plcoding.weatherapp.data.repository.WeatherRepository
import com.plcoding.weatherapp.utils.DEFAULT_WEATHER_DESTINATION
import com.plcoding.weatherapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val context: Context,
    private val repository: WeatherRepository,
    private val locationRepository: LocationRepository,
): ViewModel() {
    private val _uiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> =
        mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    private val _beforeSearchTextState: MutableState<String> =
        mutableStateOf(value = "VinhPhuc")
    val beforeSearchTextState: State<String> = _beforeSearchTextState

    private val _searchHistory: MutableState<List<String>> = mutableStateOf(getSearchHistory())
    val searchHistory: State<List<String>> = _searchHistory

    private val _currentScreen = mutableStateOf(WeatherScreenType.List)
    val currentScreen: State<WeatherScreenType> = _currentScreen

    fun setCurrentScreen(screenType: WeatherScreenType) {
        _currentScreen.value = screenType
    }

    ////////////////////////////////////
    private val _weatherSelectionList = mutableStateListOf<WeatherUiState>()
    val weatherSelectionList: List<WeatherUiState>
        get() = _weatherSelectionList

    init {
        _weatherSelectionList.addAll(getWeatherListFromSharedPreferences())
    }

    fun addCityToWeatherList(newWeatherUiState: WeatherUiState) {
        val existingCity = _weatherSelectionList.find { it.weather?.name == newWeatherUiState.weather?.name }
        if (existingCity == null) {
            _weatherSelectionList.add(newWeatherUiState)
            saveWeatherListToSharedPreferences()
        }
    }

    fun removeCityFromWeatherList(weatherUiState: WeatherUiState) {
        if (weatherUiState.weather?.name != "Vinh Phuc"){
            _weatherSelectionList.remove(weatherUiState)
            saveWeatherListToSharedPreferences()
        }
    }

    private fun saveWeatherListToSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("WeatherData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(_weatherSelectionList)
        editor.putString("weatherList", json)
        editor.apply()
    }

    fun getWeatherListFromSharedPreferences(): List<WeatherUiState> {
        val sharedPreferences = context.getSharedPreferences("WeatherData", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("weatherList", null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, Array<WeatherUiState>::class.java).toList()
        } else {
            emptyList()
        }
    }
    ////////////////////////////////////

    private fun getSearchHistory(): List<String>{
        val sharedPreferences = context.getSharedPreferences("WeatherSearchHistory", Context.MODE_PRIVATE)
        val searchHistoryJson = sharedPreferences.getString("searchHistory", "[]")
        val gson = Gson()
        return gson.fromJson(searchHistoryJson, Array<String>::class.java).toList()
    }

    private fun saveSearchHistory(searchHistory: List<String>){
        val gson = Gson()
        val searchHistoryJson = gson.toJson(searchHistory)
        val sharedPreferences = context.getSharedPreferences("WeatherSearchHistory", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()){
            putString("searchHistory", searchHistoryJson)
            apply()
        }
    }

    fun addToSearchHistory(query: String){
        val newSearchHistory = listOf(query) + _searchHistory.value.filter { it != query }
        _searchHistory.value = newSearchHistory
        saveSearchHistory(newSearchHistory)
    }
    ////////////////////////////////

    fun setBeforeSearchTextState(newValue: String) {
        _beforeSearchTextState.value = newValue
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState){
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String){
        _searchTextState.value = newValue
    }

    private var cachedWeatherUiState: WeatherUiState? = null



    fun getWeather(city: String = DEFAULT_WEATHER_DESTINATION){
        repository.getWeatherForecast(city).map { result ->
            when (result){
                is Result.Success ->{
                    _uiState.value = WeatherUiState(weather = result.data)
                    cachedWeatherUiState = _uiState.value
                    setBeforeSearchTextState(_searchTextState.value)//ho tro nut Back
                    saveWeatherUiStateToLocalStorage(_uiState.value)//ho tro hien khi khong co internet
                    addToSearchHistory(city)//
                    setCurrentScreen(WeatherScreenType.Detail)
                }

                is Result.Error ->{
                    _uiState.value = WeatherUiState(errorMessage = result.errorMessage)
                }

                is Result.Loading ->{
                    _uiState.value = WeatherUiState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

    fun getWeatherAtCurrentLocation(){
        viewModelScope.launch {
            locationRepository.getCurrentLocation()?.let{location ->
                val destination = location.latitude.toString() + ',' + location.longitude.toString()
                //setBeforeSearchTextState(destination)
                getWeather(destination)
            } ?: kotlin.run {
                _uiState.value = WeatherUiState(errorMessage = "Couldn't retrieve location. Make sure to grant permission and enable GPS.")
            }
        }
    }

    private fun saveWeatherUiStateToLocalStorage(weatherUiState: WeatherUiState){
        val gson = Gson()
        val json = gson.toJson(weatherUiState)

        val sharedPreferences = context.getSharedPreferences("WeatherUiState", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit();
        editor.putString("cachedWeatherUiState", json);
        editor.apply();
    }

    fun getWeatherUiStateFromLocalStorage(): WeatherUiState? {
        val sharedPreferences = context.getSharedPreferences("WeatherUiState", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("cachedWeatherUiState", null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, WeatherUiState::class.java)
        } else {
            null
        }
    }


    override fun onCleared() {
        super.onCleared()
        cachedWeatherUiState?.let {
            saveWeatherUiStateToLocalStorage(it)
        }
    }

}

