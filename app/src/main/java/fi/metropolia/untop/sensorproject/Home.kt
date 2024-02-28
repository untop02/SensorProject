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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun Home(modifier: Modifier, viewModel: MyViewModel, navController: NavHostController) {
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
                    observedValue = viewModel.test1Data.observeAsState(0),
                    navController = navController
                )
                CustomCard(
                    modifier = modifier,
                    name = "Humidity",
                    observedValue = viewModel.test2Data.observeAsState(0),
                    navController = navController
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
                    name = "Sensor stuff",
                    observedValue = viewModel.test3Data.observeAsState(0),
                    navController = navController
                )
                CustomCard(
                    modifier = modifier,
                    name = "Sensor stuff",
                    observedValue = viewModel.test4Data.observeAsState(0),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun CustomCard(
    modifier: Modifier,
    name: String,
    observedValue: State<Int>,
    navController: NavHostController
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .width(150.dp)
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
            text = observedValue.value.toString(), modifier = modifier
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
/*
@Composable
fun ElevatedCard(
    elevation: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = elevation
    ) {
        content()
    }
}*/
