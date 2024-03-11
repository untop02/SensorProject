package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fi.metropolia.untop.sensorproject.data.MyViewModel
import fi.metropolia.untop.sensorproject.data.OfflineRepo
import fi.metropolia.untop.sensorproject.data.SensorDatabase
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun Home(modifier: Modifier, viewModel: MyViewModel, navController: NavHostController) {
    Column {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                val sensors = listOf(
                    SensorData(R.string.home_name_temp,"Temperature", viewModel.ambientTemp," °C"),
                    SensorData(R.string.home_name_hum, "Humidity", viewModel.humidity, " %"),
                    SensorData(R.string.home_name_pres, "Pressure", viewModel.pressure, " hPa"),
                    SensorData(R.string.home_name_illum, "Illuminance", viewModel.light, " lx")
                )
                Text(
                    text = stringResource(id = R.string.row_name_phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                for (chunk in sensors.chunked(2)) {
                    SensorRow(chunk, navController, viewModel)
                }
            }
        }
        Weather(modifier = Modifier, viewModel = viewModel)
    }
}

@Composable
fun SensorRow(
    sensors: List<SensorData>,
    navController: NavHostController,
    viewModel: MyViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (sensor in sensors) {
            CustomCard(
                modifier = if (viewModel.nullSensors.value?.contains(sensor.idName) == true) Modifier.disabled() else Modifier,
                name = stringResource(id = sensor.nameResId),
                value = sensor.value.observeAsState(0.0),
                navController = navController,
                unit = sensor.unit
            )
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

class GreyScaleModifier : DrawModifier {
    override fun ContentDrawScope.draw() {
        val saturationMatrix = ColorMatrix().apply { setToSaturation(0f) }
        val saturationFilter = ColorFilter.colorMatrix(saturationMatrix)
        val paint = Paint().apply {
            colorFilter = saturationFilter
        }
        drawIntoCanvas {
            it.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            it.restore()
        }
    }
}

fun Modifier.disabled() = this
    .then(GreyScaleModifier())
    .then(alpha(0.4f))

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePrev() {
    val context = LocalContext.current
    val sensorDatabase = SensorDatabase.getDatabase(context)
    val navController = rememberNavController()
    Home(
        modifier = Modifier, viewModel = MyViewModel(
            OfflineRepo(
                sensorDatabase.itemDao(),
                sensorDatabase.settingsDao()
            )
        ), navController = navController
    )
}

data class SensorData(
    val nameResId: Int,
    val idName: String,
    val value: MutableLiveData<Double>,
    val unit: String
)
