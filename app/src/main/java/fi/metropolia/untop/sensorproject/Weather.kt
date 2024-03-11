package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.metropolia.untop.sensorproject.data.MyViewModel


@Composable
fun Weather(modifier: Modifier, viewModel: MyViewModel) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = viewModel.weatherData.observeAsState().value.toString()
        )
    }
}
