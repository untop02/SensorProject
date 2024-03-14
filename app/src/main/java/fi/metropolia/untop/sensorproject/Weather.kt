package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.untop.sensorproject.data.MyViewModel


@Composable
fun Weather(modifier: Modifier, viewModel: MyViewModel) {
    val weatherData = viewModel.weatherData.observeAsState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
    ) {
        //Homescreen weather apis title "Outside"
        Text(
            text = stringResource(id = R.string.row_name_out),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        val sensors = listOf(
            ApiSensorData(
                R.string.home_name_temp,
                stringResource(id = R.string.home_name_temp),
                weatherData.value?.main?.temp.toString(),
                " °C"
            ), ApiSensorData(
                R.string.home_name_hum,
                stringResource(id = R.string.home_name_hum),
                weatherData.value?.main?.humidity.toString(),
                " %"
            ), ApiSensorData(
                R.string.home_name_pres,
                stringResource(id = R.string.home_name_pres),
                weatherData.value?.main?.pressure.toString(),
                " hPa"
            ), ApiSensorData(
                R.string.home_name_illum,
                stringResource(id = R.string.home_name_feels),
                weatherData.value?.main?.feels_like.toString(),
                " °C"
            )
        )
        for (chunk in sensors.chunked(2)) {
            ApiSensorRow(chunk)
        }
    }
}

@Composable
fun ApiSensorRow(sensors: List<ApiSensorData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (sensor in sensors) {
            ApiCard(
                modifier = Modifier, name = sensor.idName, value = sensor.value, unit = sensor.unit
            )
        }
    }
}

//Layout for api data cards displaying temp, humidity...
@Composable
fun ApiCard(
    modifier: Modifier, name: String, value: String, unit: String
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), modifier = modifier.width(170.dp)
    ) {
        Text(
            text = name,
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (value.isEmpty() or (value == "null")) {
                stringResource(R.string.loading)
            } else {
                value + unit
            },
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}

data class ApiSensorData(
    val nameResId: Int, val idName: String, val value: String, val unit: String
)