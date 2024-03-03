package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun Home(modifier: Modifier, viewModel: MyViewModel, navController: NavHostController) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                CustomCard(
                    modifier = modifier,
                    name = "Temperature",
                    value = viewModel.ambientTemp.observeAsState(0.0),
                    navController = navController,
                    unit = " °C"
                )
                CustomCard(
                    modifier = modifier,
                    name = "Humidity",
                    value = viewModel.humidity.observeAsState(0.0),
                    navController = navController,
                    unit = " %"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                CustomCard(
                    modifier = modifier,
                    name = "Ambient air pressure",
                    value = viewModel.pressure.observeAsState(0.0),
                    navController = navController,
                    unit = " hPa"
                )
                CustomCard(
                    modifier = modifier,
                    name = "Illuminance",
                    value = viewModel.light.observeAsState(0.0),
                    navController = navController,
                    unit = " lx"
                )
            }
            Weather(modifier = Modifier, context = context, viewModel = viewModel)
        }
    }
}

@Composable
fun CustomCard(
    modifier: Modifier,
    name: String,
    value: State<Double>,
    navController: NavHostController,
    unit: String
) {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .width(170.dp)
            .clickable { navController.navigate(Destinations.Graph.route.plus("?observedName=$name")) }
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
            text = df.format(value.value) + unit, modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally), textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePrev() {
    val navController = rememberNavController()
    Home(modifier = Modifier, viewModel = MyViewModel(), navController = navController)
}
