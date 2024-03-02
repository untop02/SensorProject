package fi.metropolia.untop.sensorproject.graphs

import android.graphics.PorterDuff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.lineSpec
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.shape.dashedShape
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.component.shape.shader.fromComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.shape.shader.TopBottomShader
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import fi.metropolia.untop.sensorproject.R
import fi.metropolia.untop.sensorproject.rememberMarker


@Composable
fun ComposeChart9(
    modelProducer: CartesianChartModelProducer,
) {
    val marker = rememberMarker()
    ProvideChartStyle() {
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines =
                    listOf(
                        lineSpec(
                            shader =
                            TopBottomShader(
                                DynamicShaders.color(chartColors[0]),
                                DynamicShaders.color(chartColors[1]),
                            ),
                            backgroundShader =
                            TopBottomShader(
                                DynamicShaders.composeShader(
                                    DynamicShaders.fromComponent(
                                        componentSize = 6.dp,
                                        component =
                                        rememberShapeComponent(
                                            shape = Shapes.pillShape,
                                            color = chartColors[0],
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
                                            color = chartColors[1],
                                            margins = remember { dimensionsOf(horizontal = 2.dp) },
                                        ),
                                        checkeredArrangement = false,
                                    ),
                                    DynamicShaders.verticalGradient(
                                        arrayOf(Color.Transparent, Color.Black),
                                    ),
                                    PorterDuff.Mode.DST_IN,
                                ),
                            ),
                        ),
                    ),
                ),
                startAxis =
                rememberStartAxis(
                    label =
                    axisLabelComponent(
                        color = MaterialTheme.colorScheme.onBackground,
                        background =
                        rememberShapeComponent(
                            shape = Shapes.pillShape,
                            color = Color.Transparent,
                            strokeColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeWidth = 1.dp,
                        ),
                        padding = remember { dimensionsOf(horizontal = 6.dp, vertical = 2.dp) },
                        margins = remember { dimensionsOf(end = 8.dp) },
                    ),
                    axis = null,
                    tick = null,
                    guideline =
                    rememberLineComponent(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape =
                        remember {
                            Shapes.dashedShape(
                                shape = Shapes.pillShape,
                                dashLength = 4.dp,
                                gapLength = 8.dp,
                            )
                        },
                    ),
                    itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 4 }) },
                ),
                bottomAxis =
                rememberBottomAxis(
                    guideline = null,
                    itemPlacer =
                    remember {
                        AxisItemPlacer.Horizontal.default(
                            spacing = 3,
                            addExtremeLabelPadding = true,
                        )
                    },
                ),
            ),
            modelProducer = modelProducer,
            marker = marker,
            runInitialAnimation = false,
            horizontalLayout = HorizontalLayout.fullWidth(),
        )
    }
}

private val chartColors
    @ReadOnlyComposable
    @Composable
    get() =
        listOf(
            colorResource(id = R.color.teal_200),
            colorResource(id = R.color.black),
        )