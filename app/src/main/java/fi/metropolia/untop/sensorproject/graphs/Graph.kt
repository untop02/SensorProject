package fi.metropolia.untop.sensorproject.graphs

import android.graphics.Typeface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.lineSeries
import fi.metropolia.untop.sensorproject.R
import fi.metropolia.untop.sensorproject.data.Item
import fi.metropolia.untop.sensorproject.data.MyViewModel
import fi.metropolia.untop.sensorproject.data.OfflineRepo
import fi.metropolia.untop.sensorproject.data.SensorDatabase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

enum class TimeInterval {
    DAY, WEEK, MONTH, All
}

@Composable
fun Graph(modifier: Modifier, viewModel: MyViewModel, name: String?) {
    var selectedTimeInterval by remember { mutableStateOf(TimeInterval.All) }
    val names = listOf(
        stringResource(id = R.string.home_name_temp),
        stringResource(id = R.string.home_name_illum),
        stringResource(id = R.string.home_name_pres),
        stringResource(id = R.string.home_name_hum),
        stringResource(id = R.string.graph_name_apiTemp),
        stringResource(id = R.string.graph_name_apiHumi),
        stringResource(id = R.string.graph_name_apiPress),
    )
    val history by viewModel.history.observeAsState(emptyList())

    var filteredHistory by rememberSaveable { mutableStateOf(history) }

    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(history, filteredHistory) {
        modelProducer.tryRunTransaction {
            val dataTemperature = filteredHistory.map { it.temperature }
            val dataHumidity =  filteredHistory.map { it.humidity }
            val dataPressure = filteredHistory.map { it.pressure }
            val dataIlluminance =   filteredHistory.map { it.illuminance }
            val dataApiTemperature = filteredHistory.map { it.temperatureAPI }
            val dataApiHumidity = filteredHistory.map { it.humidityAPI }
            val dataApiPressure = filteredHistory.map { it.pressureAPI }

            when (name) {
                "Temperature" -> lineSeries { series(dataTemperature) }
                "Humidity" -> columnSeries { series(dataHumidity) }
                "Pressure" -> lineSeries { series(dataPressure) }
                "Illuminance" -> columnSeries { series(dataIlluminance) }
                else -> {
                    lineSeries { series(dataTemperature) }
                    columnSeries { series(dataIlluminance) }
                    lineSeries { series(dataPressure) }
                    columnSeries { series(dataHumidity) }
                    lineSeries { series(dataApiTemperature) }
                    columnSeries { series(dataApiHumidity) }
                    lineSeries { series(dataApiPressure) }
                }
            }
        }
    }

    LaunchedEffect(selectedTimeInterval, history) {
        filteredHistory = when (selectedTimeInterval) {
            TimeInterval.DAY -> getHistoryForDay(history)
            TimeInterval.WEEK -> getHistoryForWeek(history)
            TimeInterval.MONTH -> getHistoryForMonth(history)
            TimeInterval.All -> history
        }
    }
    Column(
        modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = listOf(
                            LineCartesianLayer.LineSpec(
                                shader = DynamicShaders.color(
                                    colorRed
                                )
                            )
                        )
                    ),
                    rememberColumnCartesianLayer(
                        columns = listOf(rememberLineComponent(color = colorBlue))
                    ),
                    rememberLineCartesianLayer(
                        lines = listOf(
                            LineCartesianLayer.LineSpec(
                                shader = DynamicShaders.color(
                                    colorGreen
                                )
                            )
                        )
                    ),
                    rememberColumnCartesianLayer(
                        columns = listOf(rememberLineComponent(color = colorPink))
                    ),
                    startAxis = rememberStartAxis(
                        label = rememberTextComponent(
                            color = MaterialTheme.colorScheme.secondary,
                        ), itemPlacer = remember {
                            AxisItemPlacer.Vertical.default(maxItemCount = { 5 })
                        }),
                    //bottomAxis = rememberBottomAxis(guideline = null, valueFormatter = { it, _, _ -> it.toString() }),
                    bottomAxis = rememberBottomAxis(guideline = null),
                    legend = rememberLegend(name, names)
                ),
                modelProducer = modelProducer,
                modifier = modifier.fillMaxHeight(),
                marker = rememberMarker(),
                chartScrollState = rememberChartScrollState(),
                isZoomEnabled = true
            )
        }
        Row {
            TimeButton(
                interval = TimeInterval.DAY,
                selectedInterval = selectedTimeInterval,
                onClick = { selectedTimeInterval = TimeInterval.DAY },
                text = stringResource(R.string.day)
            )

            TimeButton(
                interval = TimeInterval.WEEK,
                selectedInterval = selectedTimeInterval,
                onClick = { selectedTimeInterval = TimeInterval.WEEK },
                text = stringResource(R.string.week)
            )

            TimeButton(
                interval = TimeInterval.MONTH,
                selectedInterval = selectedTimeInterval,
                onClick = { selectedTimeInterval = TimeInterval.MONTH },
                text = stringResource(R.string.month)
            )

            TimeButton(
                interval = TimeInterval.All,
                selectedInterval = selectedTimeInterval,
                onClick = { selectedTimeInterval = TimeInterval.All },
                text = stringResource(R.string.all)
            )
        }
    }
}

@Composable
fun TimeButton(
    interval: TimeInterval,
    selectedInterval: TimeInterval,
    onClick: () -> Unit,
    text: String
) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedInterval == interval) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun rememberLegend(name: String?, names: List<String>) =
    horizontalLegend(
        items = if (name == null) {
            chartColors.mapIndexed { index, chartColor ->
                legendItem(
                    icon = rememberShapeComponent(Shapes.pillShape, chartColor),
                    label =
                    rememberTextComponent(
                        color = currentChartStyle.axis.axisLabelColor,
                        textSize = legendItemLabelTextSize,
                        typeface = Typeface.MONOSPACE,
                    ),
                    labelText = names[index],
                )
            }
        } else {
            listOf(
                legendItem(
                    icon = rememberShapeComponent(Shapes.pillShape, chartColors.first()),
                    label =
                    rememberTextComponent(
                        color = currentChartStyle.axis.axisLabelColor,
                        textSize = legendItemLabelTextSize,
                        typeface = Typeface.MONOSPACE,
                    ),
                    labelText = name,
                )
            )
        },
        iconSize = legendItemIconSize,
        iconPadding = legendItemIconPaddingValue,
        spacing = legendItemSpacing,
        padding = legendPadding,
    )

private val colorBlue = Color(0xff0000ff)
private val colorPink = Color(0xffff00ff)
private val colorGreen = Color(0xFF00FF00)
private val colorRed = Color(0xFFFF0000)
private val colorYellow = Color(0xFFFFEB3B)
private val colorViolet = Color(0xFF651FFF)
private val colorOrange = Color(0xFFDD5B00)
val chartColors = listOf(colorRed, colorBlue, colorGreen, colorPink, colorYellow, colorViolet, colorOrange)
private val legendItemLabelTextSize = 12.sp
private val legendItemIconSize = 8.dp
private val legendItemIconPaddingValue = 10.dp
private val legendItemSpacing = 4.dp
private val legendTopPaddingValue = 8.dp
private val legendPadding = dimensionsOf(top = legendTopPaddingValue)

private fun getHistoryForDay(history: List<Item>): List<Item> {
    val today = LocalDate.now()
    return history.filter { LocalDate.parse(it.date.split(" ")[0]) == today }
}

private fun getHistoryForWeek(history: List<Item>): List<Item> {
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    return history.filter {
        val itemDate =
            LocalDate.parse(it.date.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        itemDate in startOfWeek..endOfWeek
    }
}

private fun getHistoryForMonth(history: List<Item>): List<Item> {
    val today = LocalDate.now()
    val startOfMonth = today.withDayOfMonth(1)
    val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
    return history.filter {
        val itemDate =
            LocalDate.parse(it.date.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        itemDate in startOfMonth..endOfMonth
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GraphPreview() {
    val sensorDatabase = SensorDatabase.getDatabase(LocalContext.current)
    Graph(
        modifier = Modifier, viewModel = MyViewModel(
            OfflineRepo(
                sensorDatabase.itemDao(),
                sensorDatabase.settingsDao()
            )
        ), name = "Temperature"
    )
}