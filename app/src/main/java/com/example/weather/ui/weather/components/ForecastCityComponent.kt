package com.example.weather.ui.weather.components


import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weather.R
import com.plcoding.weatherapp.model.Weather
import com.plcoding.weatherapp.ui.theme.WeatherTheme
import com.plcoding.weatherapp.ui.weather.WeatherUiState
import com.plcoding.weatherapp.ui.weather.components.ForecastComponent
import com.plcoding.weatherapp.utils.DateUtil.toFormattedDay
import java.time.LocalDateTime

@Composable
fun ForecastCityComponent(
    modifier: Modifier = Modifier,
    uiState: WeatherUiState,
    onDeleteClick: () -> Unit,
    onCitySelected: () -> Unit
){
    ElevatedCard(
        modifier = modifier.padding(end = 16.dp)
            .clickable { onCitySelected() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .width(150.dp),

                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,

            ) {
                Text(
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    text = uiState.weather?.name.orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                            R.string.temperature_value_in_celsius,
                            uiState.weather?.forecasts?.get(0)?.minTemp.orEmpty()
                    ) + " / " +
                        stringResource(
                            R.string.temperature_value_in_celsius,
                            uiState.weather?.forecasts?.get(0)?.maxTemp.orEmpty()
                        ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(
                        R.string.temperature_value_in_celsius,
                        uiState.weather?.forecasts?.get(0)?.hour?.get(LocalDateTime.now().hour)?.temperature.orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
//                Text(
//                    modifier = Modifier.padding(horizontal = 8.dp),
//                    style = MaterialTheme.typography.titleMedium,
//                    text = stringResource(id = R.string.delete),
//                    fontWeight = FontWeight.Bold
//                )
            }

        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForecastCityComponentPreview() {
    Surface {
        WeatherTheme {
            ForecastCityComponent(
               uiState = WeatherUiState(
                   weather = null
               ),
                onDeleteClick = {},
                onCitySelected = {}
            )
        }
    }
}