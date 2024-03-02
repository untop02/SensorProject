package fi.metropolia.untop.sensorproject.graphs

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import fi.metropolia.untop.sensorproject.rememberMarker

@Composable
fun ComposeChart3(
    modelProducer: CartesianChartModelProducer,
) {
    ProvideChartStyle() {
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(axisValueOverrider = axisValueOverrider),
                startAxis =
                rememberStartAxis(
                    guideline = null,
                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                    titleComponent =
                    rememberTextComponent(
                        color = Color.Black,
                        background = rememberShapeComponent(Shapes.pillShape, color1),
                        padding = axisTitlePadding,
                        margins = startAxisTitleMargins,
                        typeface = Typeface.MONOSPACE,
                    ),
                    title = "Y",
                ),
                bottomAxis =
                rememberBottomAxis(
                    titleComponent =
                    rememberTextComponent(
                        background = rememberShapeComponent(Shapes.pillShape, color2),
                        color = Color.White,
                        padding = axisTitlePadding,
                        margins = bottomAxisTitleMargins,
                        typeface = Typeface.MONOSPACE,
                    ),
                    title = "X",
                ),
                fadingEdges = rememberFadingEdges(),
            ),
            modelProducer = modelProducer,
            marker = rememberMarker(MarkerComponent.LabelPosition.AroundPoint),
            runInitialAnimation = false,
            horizontalLayout = horizontalLayout,
        )
    }
}

private const val COLOR_1_CODE = 0xffffbb00
private const val COLOR_2_CODE = 0xff9db591
private const val AXIS_VALUE_OVERRIDER_Y_FRACTION = 1.2f

private val color1 = Color(COLOR_1_CODE)
private val color2 = Color(COLOR_2_CODE)
private val chartColors = listOf(color1, color2)
private val axisValueOverrider =
    AxisValueOverrider.adaptiveYValues<LineCartesianLayerModel>(
        yFraction = AXIS_VALUE_OVERRIDER_Y_FRACTION,
        round = true,
    )
private val axisTitleHorizontalPaddingValue = 8.dp
private val axisTitleVerticalPaddingValue = 2.dp
private val axisTitlePadding =
    dimensionsOf(axisTitleHorizontalPaddingValue, axisTitleVerticalPaddingValue)
private val axisTitleMarginValue = 4.dp
private val startAxisTitleMargins = dimensionsOf(end = axisTitleMarginValue)
private val bottomAxisTitleMargins = dimensionsOf(top = axisTitleMarginValue)
private val horizontalLayout = HorizontalLayout.fullWidth()