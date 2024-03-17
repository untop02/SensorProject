package fi.metropolia.untop.sensorproject

import android.bluetooth.BluetoothAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import fi.metropolia.untop.sensorproject.data.MyViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

lateinit var nullSensors: State<List<String>?>

@Composable
fun Home(
    modifier: Modifier,
    viewModel: MyViewModel,
    navController: NavHostController,
    bluetoothAdapter: BluetoothAdapter,
    requestPermissionsLauncher: ActivityResultLauncher<Array<String>>,
    requiredPermissions: HashMap<String, Boolean>,

    ) {
    nullSensors = viewModel.nullSensors.observeAsState()
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
                    SensorData(
                        R.string.home_name_temp,
                        "Temperature",
                        viewModel.ambientTemp,
                        " °C"
                    ),
                    SensorData(R.string.home_name_hum, "Humidity", viewModel.humidity, " %"),
                    SensorData(R.string.home_name_pres, "Pressure", viewModel.pressure, " hPa"),
                    SensorData(R.string.home_name_illum, "Illuminance", viewModel.light, " lx")
                )
                //Homescreen title "Phone"
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
                    SensorRow(chunk, navController)
                }
            }
        }
        Weather(modifier = Modifier, viewModel = viewModel)
        BluetoothList(
            bluetoothAdapter = bluetoothAdapter,
            viewModel = viewModel,
            requiredPermissions = requiredPermissions,
            requestPermissionsLauncher = requestPermissionsLauncher,
        )
    }
}

@Composable
fun SensorRow(
    sensors: List<SensorData>,
    navController: NavHostController,
) {
    var showAlert by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (sensor in sensors) {
            val isEnabled = nullSensors.value?.contains(sensor.idName) != true
            CustomCard(
                modifier = Modifier
                    .width(170.dp)
                    .then(if (isEnabled) Modifier else Modifier.disabled())
                    .clickable {
                        if (isEnabled) {
                            navController.navigate(Destinations.Graph.route.plus("?observedName=${sensor.idName}"))
                        } else {
                            showAlert = true
                        }
                    },
                name = stringResource(id = sensor.nameResId),
                value = sensor.value.observeAsState(0.0),
                unit = sensor.unit,
            )
        }
        if (showAlert) {
            NoSensorAlert(onDismiss = { showAlert = false })
        }
    }
}

//Layout for phone sensor data cards displaying temp, humidity...
@Composable
fun CustomCard(
    modifier: Modifier,
    name: String,
    value: State<Double>,
    unit: String,
) {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = df.format(value.value) + unit, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally), textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoSensorAlert(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Info icon")
        },
        text = {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.alert_desc)
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = "OK")
                }
            }
        }
    )
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

data class SensorData(
    val nameResId: Int,
    val idName: String,
    val value: MutableLiveData<Double>,
    val unit: String
)
