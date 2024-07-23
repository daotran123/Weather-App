package com.example.weather.ui.weather

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.ui.weather.components.HourlyComponent
import com.example.weather.ui.weather.components.WeatherComponent
import com.plcoding.weatherapp.ui.weather.SearchWidgetState
import com.plcoding.weatherapp.ui.weather.WeatherUiState
import com.plcoding.weatherapp.ui.weather.WeatherViewModel
import com.plcoding.weatherapp.ui.weather.components.Animation
import com.plcoding.weatherapp.ui.weather.components.ForecastComponent
import com.plcoding.weatherapp.utils.DateUtil.toFormattedDay
import kotlinx.coroutines.flow.first
import java.util.Locale

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
    cityName: String,
    onBack: () -> Unit
){
    BackHandler {
        onBack()
    }
    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState
    val uiState: WeatherUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.getWeatherAtCurrentLocation()
        }
    }

    LaunchedEffect(cityName) {
        viewModel.getWeather(city = cityName)
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ButtonWithPermissionRequest(
                    modifier = Modifier,
                    permissionLauncher = permissionLauncher
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location"
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = stringResource(id = R.string.location),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.addCityToWeatherList(uiState)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Save"
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = stringResource(id = R.string.save),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@Composable
fun ButtonWithPermissionRequest(
    modifier: Modifier = Modifier,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    ) {
        content()
    }
}

@Composable
fun WeatherScreenContent(
    uiState: WeatherUiState,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel?,
){
    when{
        uiState.isLoading -> {
            Animation(
                modifier = Modifier.fillMaxSize(),
                animation = R.raw.animation_loading
            )
        }

        uiState.errorMessage.isNotEmpty() -> {
            WeatherErrorState(
                uiState = uiState,
                viewModel = viewModel
            )
        }

        else -> {
            WeatherSuccessState(
                modifier = modifier,
                uiState = uiState
            )
            //viewModel?.setBeforeSearchTextState(viewModel.searchTextState.value)
        }
    }
}

@Composable
fun WeatherErrorState(
    modifier: Modifier = Modifier,
    uiState: WeatherUiState,
    viewModel: WeatherViewModel?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Animation(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f),
            animation = R.raw.animation_error
        )
        Button(onClick = {viewModel?.getWeather(viewModel.beforeSearchTextState.value)}) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )

            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(id = R.string.back),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            modifier = modifier
                .weight(2f)
                .alpha(0.5f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            text = "Something went wrong: ${uiState.errorMessage}",
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun WeatherSuccessState(
    modifier: Modifier,
    uiState: WeatherUiState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = uiState.weather?.name.orEmpty(),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = uiState.weather?.date?.toFormattedDay().orEmpty(),
            style = MaterialTheme.typography.bodyLarge
        )

        AsyncImage(
            modifier = Modifier.size(64.dp),
            model = stringResource(
                R.string.icon_image_url,
                uiState.weather?.condition?.icon.orEmpty()
            ),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            error = painterResource(id = R.drawable.ic_placeholder),
            placeholder = painterResource(id = R.drawable.ic_placeholder)
        )
        Text(
            text = stringResource(
                R.string.temperature_value_in_celsius,
                uiState.weather?.temperature.toString()
            ) ,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            text = uiState.weather?.condition?.text.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(
                R.string.feels_like_temperature_in_celsius,
                uiState.weather?.feelsLike.toString()
            ),
            style = MaterialTheme.typography.bodySmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sunrise),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = uiState.weather?.forecasts?.get(0)?.sunrise?.lowercase(Locale.US).orEmpty(),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_sunset),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = uiState.weather?.forecasts?.get(0)?.sunset?.lowercase(Locale.US).orEmpty(),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
        ) {
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherLabel = stringResource(R.string.wind_speed_label),
                weatherValue = uiState.weather?.wind.toString(),
                weatherUnit = stringResource(R.string.wind_speed_unit),
                iconId = R.drawable.ic_wind,
            )
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherLabel = stringResource(R.string.uv_index_label),
                weatherValue = uiState.weather?.uv.toString(),
                weatherUnit = stringResource(R.string.uv_unit),
                iconId = R.drawable.ic_uv,
            )
            WeatherComponent(
                modifier = Modifier.weight(1f),
                weatherLabel = stringResource(R.string.humidity_label),
                weatherValue = uiState.weather?.humidity.toString(),
                weatherUnit = stringResource(R.string.humidity_unit),
                iconId = R.drawable.ic_humidity,
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.today),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
        ) {
            uiState.weather?.forecasts?.get(0)?.let { forecast ->
                items(forecast.hour) { hour ->
                    HourlyComponent(
                        time = hour.time,
                        icon = hour.icon,
                        temperature = stringResource(
                            R.string.temperature_value_in_celsius,
                            hour.temperature
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.forecast),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
        ) {
            uiState.weather?.let { weather ->
                items(weather.forecasts) { forecast ->
                    ForecastComponent(
                        date = forecast.date,
                        icon = forecast.icon,
                        minTemp = stringResource(
                            R.string.temperature_value_in_celsius,
                            forecast.minTemp
                        ),
                        maxTemp = stringResource(
                            R.string.temperature_value_in_celsius,
                            forecast.maxTemp,
                        ),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

    }
}

@Composable
fun WeatherTopAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    searchHistory: List<String>,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,

){
    when (searchWidgetState){
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered
            )
        }

        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked,
                searchHistory = searchHistory,
                onSuggestionClicked = onSearchClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    onSearchClicked: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        actions = {
            IconButton(onClick = {onSearchClicked()}) {
                Icon(
                    imageVector = Icons.Filled.Search, 
                    contentDescription = stringResource(id = R.string.search_icon)
                )
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    searchHistory: List<String>,
    onSuggestionClicked: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val suggestions = searchHistory.filter { it.contains(text, ignoreCase = true) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange =  {
                    onTextChange(it)
                    expanded = it.isNotEmpty() && suggestions.isNotEmpty()
                },
                placeholder = {
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = stringResource(id = R.string.search_hint)
                    )
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                ),
                singleLine = true,
                leadingIcon = {
                    IconButton(
                        modifier = Modifier.alpha(0.7f),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon)
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()){
                                onTextChange("")
                                expanded = false
                            }else{
                                onCloseClicked()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close_icon)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClicked(text)
                        keyboardController?.hide()
                        expanded = false
                    }
                )
            )
        }

        if (expanded){
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = {expanded = false}
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        suggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    onSuggestionClicked(suggestion)
                                    onTextChange(suggestion)
                                    keyboardController?.hide()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}


//@Composable
//@Preview
//fun DefaultAppBarPreview() {
//    DefaultAppBar(onSearchClicked = {})
//}

//@Composable
//@Preview
//fun SearchAppBarPreview() {
//    SearchAppBar(
//        text = "Search for a city",
//        onTextChange = {},
//        onCloseClicked = {},
//        onSearchClicked = {}
//
//    )
//}