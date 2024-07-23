package com.example.weather.ui.weather

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.R
import com.plcoding.weatherapp.ui.weather.SearchWidgetState
import com.plcoding.weatherapp.ui.weather.WeatherUiState
import com.plcoding.weatherapp.ui.weather.WeatherViewModel

@Composable
fun WeatherNoInternet(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
    uiState: WeatherUiState,
    onBack: () -> Unit
){
    BackHandler {
        onBack()
    }
    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState
    val searchHistory by viewModel.searchHistory
    //val uiState: WeatherUiState = viewModel.getCachedWeatherUiState() ?: WeatherUiState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.getWeatherAtCurrentLocation()
        }
    }

    Scaffold(
        topBar = {
            WeatherTopAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                searchHistory = searchHistory,
                onTextChange = {viewModel.updateSearchTextState(it)},
                onCloseClicked = { viewModel.updateSearchWidgetState(SearchWidgetState.CLOSED) },
                onSearchClicked = {viewModel.getWeather(it)},
                onSearchTriggered = {
                    viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                }
            )
        },
        content = {paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                WeatherScreenContent(
                    uiState = uiState,
                    modifier = modifier,
                    viewModel = viewModel
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonWithPermissionRequest(
                    modifier = Modifier,
                    permissionLauncher = permissionLauncher
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Retry"
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = stringResource(id = R.string.location),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}