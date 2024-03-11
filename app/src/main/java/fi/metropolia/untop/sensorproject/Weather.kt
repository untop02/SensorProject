package fi.metropolia.untop.sensorproject

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.untop.sensorproject.data.MyViewModel


@Composable
fun Weather(modifier: Modifier, viewModel: MyViewModel) {

    Spacer(modifier = Modifier.height(20.dp))
    Box(
        modifier = Modifier, contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("Weather", viewModel.weatherData.observeAsState().value.toString())
            Text(
                text = "Outside",
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 32.sp, // Larger font size for title
                color = Color.DarkGray, // Customize color as needed
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                ApiCard(
                    modifier = modifier,
                    name = "Temperature",
                    value = viewModel.weatherData.value?.main?.temp.toString(),
                    unit = " °C"
                )
                ApiCard(
                    modifier = modifier,
                    name = "Humidity",
                    value = viewModel.weatherData.value?.main?.humidity.toString(),
                    unit = " %"
                )


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                ApiCard(
                    modifier = modifier,
                    name = "Air pressure",
                    value = viewModel.weatherData.value?.main?.pressure.toString(),
                    unit = " hPa"
                )
                ApiCard(
                    modifier = modifier,
                    name = "Feels like",
                    value = viewModel.weatherData.value?.main?.feels_like.toString(),
                    unit = " °C"
                )
            }
        }

    }
}

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
            text = value + unit,
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}
