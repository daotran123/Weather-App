package com.example.weather.ui.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.ui.weather.components.ForecastCityComponent
import com.example.weather.utils.WeatherScreenType
import com.plcoding.weatherapp.ui.weather.SearchWidgetState
import com.plcoding.weatherapp.ui.weather.WeatherUiState
import com.plcoding.weatherapp.ui.weather.WeatherViewModel

@Composable
fun WeatherListScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
    onCitySelected: (WeatherUiState) -> Unit
) {
    var weatherSelectionList by remember { mutableStateOf(viewModel.weatherSelectionList) }

//    val searchWidgetState by viewModel.searchWidgetState
//    val searchTextState by viewModel.searchTextState
//    val uiState: WeatherUiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val searchHistory by viewModel.searchHistory
//    val currentScreen by viewModel.currentScreen

//    Scaffold(
//        topBar = {
//            WeatherTopAppBar(
//                searchWidgetState = searchWidgetState,
//                searchTextState = searchTextState,
//                searchHistory = searchHistory,
//                onTextChange = {viewModel.updateSearchTextState(it)},
//                onCloseClicked = { viewModel.updateSearchWidgetState(SearchWidgetState.CLOSED) },
//                onSearchClicked = {viewModel.getWeather(it)},
//                onSearchTriggered = {
//                    viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
//                }
//            )
//        },
//        content = {paddingValues ->
//            Surface(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                color = MaterialTheme.colorScheme.background
//            ){
//                if (currentScreen == WeatherScreenType.List){
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(weatherSelectionList) { weatherUiState ->
                            ForecastCityComponent(
                                uiState = weatherUiState,
                                onDeleteClick = {
                                    viewModel.removeCityFromWeatherList(weatherUiState)
                                    weatherSelectionList = viewModel.weatherSelectionList
                                },
                                onCitySelected = {
                                    onCitySelected(weatherUiState)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
 //               }
//                else{
//                    WeatherScreenContent(
//                        uiState = uiState,
//                        modifier = modifier,
//                        viewModel = viewModel
//                    )
//                }
//
//            }
//
//        },

 //   )

}



