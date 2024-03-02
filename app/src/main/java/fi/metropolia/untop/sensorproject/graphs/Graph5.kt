package fi.metropolia.untop.sensorproject.graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import fi.metropolia.untop.sensorproject.rememberMarker

@Composable
fun ComposeChart8(
    modelProducer: CartesianChartModelProducer,
) {
    ProvideChartStyle() {
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                    verticalAxisPosition = AxisPosition.Vertical.Start,
                ),
                rememberLineCartesianLayer(verticalAxisPosition = AxisPosition.Vertical.End),
                startAxis = rememberStartAxis(guideline = null),
                endAxis = rememberEndAxis(),
            ),
            modelProducer = modelProducer,
            marker = rememberMarker(),
            runInitialAnimation = false,
        )
    }
}

private const val COLOR_1_CODE = 0xffa55a5a
private const val COLOR_2_CODE = 0xffd3756b
private const val COLOR_3_CODE = 0xfff09b7d
private const val COLOR_4_CODE = 0xffffc3a1

private val color1 = Color(COLOR_1_CODE)
private val color2 = Color(COLOR_2_CODE)
private val color3 = Color(COLOR_3_CODE)
private val color4 = Color(COLOR_4_CODE)
private val columnChartColors = listOf(color1, color2, color3)
private val lineChartColors = listOf(color4)