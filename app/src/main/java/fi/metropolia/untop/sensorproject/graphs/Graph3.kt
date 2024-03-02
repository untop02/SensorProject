package fi.metropolia.untop.sensorproject.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.roundedCornerShape
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.copy
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import fi.metropolia.untop.sensorproject.rememberMarker

@Composable
fun ComposeChart4(
    modelProducer: CartesianChartModelProducer,
) {
    ProvideChartStyle() {
        val defaultColumns = currentChartStyle.columnLayer.columns
        val defaultLines = currentChartStyle.lineLayer.lines
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    remember(defaultColumns) {
                        defaultColumns.map {
                            LineComponent(it.color, it.thicknessDp, Shapes.roundedCornerShape(columnCornerRadius))
                        }
                    },
                ),
                rememberLineCartesianLayer(
                    remember(defaultLines) { defaultLines.map { it.copy(pointConnector = pointConnector) } },
                ),
                topAxis = rememberTopAxis(),
                endAxis = rememberEndAxis(),
            ),
            modelProducer = modelProducer,
            marker = rememberMarker(),
            runInitialAnimation = false,
        )
    }
}
private const val COLOR_1_CODE = 0xff916cda
private const val COLOR_2_CODE = 0xffd877d8
private const val COLOR_3_CODE = 0xfff094bb
private const val COLOR_4_CODE = 0xfffdc8c4

private val color1 = Color(COLOR_1_CODE)
private val color2 = Color(COLOR_2_CODE)
private val color3 = Color(COLOR_3_CODE)
private val color4 = Color(COLOR_4_CODE)
private val columnChartColors = listOf(color1, color2, color3)
private val lineChartColors = listOf(color4)
private val columnCornerRadius = 2.dp
private val pointConnector = DefaultPointConnector(cubicStrength = 0f)