package fi.metropolia.untop.sensorproject

import android.graphics.PorterDuff
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.component.shape.shader.fromComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.shape.shader.TopBottomShader
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import fi.metropolia.untop.sensorproject.graphs.ComposeChart1
import fi.metropolia.untop.sensorproject.graphs.ComposeChart3
import fi.metropolia.untop.sensorproject.graphs.ComposeChart4
import fi.metropolia.untop.sensorproject.graphs.ComposeChart7
import fi.metropolia.untop.sensorproject.graphs.ComposeChart8
import fi.metropolia.untop.sensorproject.graphs.ComposeChart9

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Graph(modifier: Modifier, viewModel: MyViewModel, name: String?) {
    val valueList = remember { mutableStateListOf<Number>() }
    val markerList = remember {
        mutableMapOf<Float, Marker>()
    }
    val index = remember { mutableIntStateOf(0) }
    val modelProducer = remember { CartesianChartModelProducer.build() }

    val data = when (name) {
        "Temperature" -> viewModel.ambientTemp.observeAsState(initial = 0).value
        "Humidity" -> viewModel.humidity.observeAsState(initial = 0).value
        //Pitää vielä säätää oikein, sitte ko tietää mitä näytetään🐨
        else -> viewModel.pressure.observeAsState(initial = 0).value
    }

    LaunchedEffect(data) {
        valueList.add(data.toFloat())
        index.intValue++
        modelProducer.tryRunTransaction { lineSeries { series(valueList) } }
    }

    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = data.toString())
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    listOf(
                        LineCartesianLayer.LineSpec(
                            shader = DynamicShaders.color(
                                Color.Green
                            ),
                            thicknessDp = 3f,
                            backgroundShader = TopBottomShader(
                                DynamicShaders.composeShader(
                                    DynamicShaders.fromComponent(
                                        componentSize = 6.dp,
                                        component =
                                        rememberShapeComponent(
                                            shape = Shapes.pillShape,
                                            color = Color.Blue,
                                            margins = remember { dimensionsOf(1.dp) },
                                        ),
                                    ),
                                    DynamicShaders.verticalGradient(
                                        arrayOf(Color.Black, Color.Transparent),
                                    ),
                                    PorterDuff.Mode.DST_IN,
                                ),
                                DynamicShaders.composeShader(
                                    DynamicShaders.fromComponent(
                                        componentSize = 5.dp,
                                        component =
                                        rememberShapeComponent(
                                            shape = Shapes.rectShape,
                                            color = Color.Transparent,
                                            margins = remember { dimensionsOf(horizontal = 2.dp) },
                                        ),
                                        checkeredArrangement = false,
                                    ),
                                    DynamicShaders.verticalGradient(
                                        arrayOf(Color.Transparent, Color.Black),
                                    ),
                                    PorterDuff.Mode.DST,
                                ),
                            ),
                            dataLabel = rememberTextComponent(
                                color = Color.Magenta,
                                textSize = 15.sp
                            )
                        )
                    )
                ),
                startAxis = rememberStartAxis(
                    label = rememberTextComponent(
                        color = Color.Blue,
                    ), itemPlacer = remember {
                        AxisItemPlacer.Vertical.default(maxItemCount = { 5 })
                    }),
                bottomAxis = rememberBottomAxis(guideline = null),
            ),
            modelProducer = modelProducer,
            marker = rememberMarker(),
        )
        Column(
            modifier.verticalScroll(rememberScrollState())
        ) {
            ComposeChart1(modelProducer = modelProducer)
            ComposeChart3(modelProducer = modelProducer)
            ComposeChart4(modelProducer = modelProducer)
            ComposeChart7(modelProducer = modelProducer)
            ComposeChart8(modelProducer = modelProducer)
            ComposeChart9(modelProducer = modelProducer)
        }

    }
}


/*CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    listOf(
                        LineCartesianLayer.LineSpec(
                            shader = DynamicShaders.color(
                                Color.Green
                            ),
                            thicknessDp = 8f,
                            backgroundShader = DynamicShaders.composeShader(
                                DynamicShaders.color(
                                    Color.Yellow
                                ), DynamicShaders.color(Color.Magenta), BlendMode.HUE
                            ),
                            dataLabel = rememberTextComponent(
                                color = Color.Magenta,
                                textSize = 15.sp
                            )
                        )
                    )
                ),
                startAxis = rememberStartAxis(label = rememberTextComponent(color = Color.Blue)),
                bottomAxis = rememberBottomAxis(guideline = null),
            ),
            modelProducer = modelProducer,
            marker = rememberMarker(),
        )*/
@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
private fun GraphTest() {
    Graph(modifier = Modifier, viewModel = MyViewModel(), name = "Temperature")
}