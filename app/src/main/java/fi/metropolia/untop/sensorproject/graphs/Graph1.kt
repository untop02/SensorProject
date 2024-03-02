package fi.metropolia.untop.sensorproject.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import fi.metropolia.untop.sensorproject.rememberMarker

@Composable
fun ComposeChart1(
    modelProducer: CartesianChartModelProducer,
) {
    val marker = rememberMarker()
    ProvideChartStyle() {
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(axisValueOverrider = axisValueOverrider),
                startAxis = rememberStartAxis(itemPlacer = startAxisItemPlacer),
                bottomAxis = rememberBottomAxis(guideline = null),
                persistentMarkers = remember(marker) { mapOf(PERSISTENT_MARKER_X to marker) },
            ),
            modelProducer = modelProducer,
            marker = marker,
        )
    }
}
private const val PERSISTENT_MARKER_X = 7f
private const val MAX_Y = 15f

private val color1 = Color(0xffa485e0)
private val chartColors = listOf(color1)
private val x = (1..50).toList()
private val axisValueOverrider = AxisValueOverrider.fixed<LineCartesianLayerModel>(maxY = MAX_Y)
private val startAxisItemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = { 6 })