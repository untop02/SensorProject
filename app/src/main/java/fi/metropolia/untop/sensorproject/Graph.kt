package fi.metropolia.untop.sensorproject

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.hd.charts.LineChartView
import com.hd.charts.common.model.ChartDataSet
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import co.yml.charts.common.model.Point as YPoint

@Composable
fun Graph(modifier: Modifier, viewModel: MyViewModel, name: String?) {
    val valueList = remember { mutableStateListOf<FloatEntry>() }
    val valueList2 = remember { mutableStateListOf<Float>() }
    val valueList3 = remember { mutableStateListOf<YPoint>() }
    val index = remember { mutableIntStateOf(0) }

    val data = when (name) {
        "Temperature" -> viewModel.test1Data.observeAsState(initial = 0).value
        "Humidity" -> viewModel.test2Data.observeAsState(initial = 0).value
        //Pitää vielä säätää oikein, sitte ko tietää mitä näytetään🐨
        else -> viewModel.test3Data.observeAsState(initial = 0).value
    }
    LaunchedEffect(data) {
        valueList.add(FloatEntry(index.intValue.toFloat(), data.toFloat()))
        valueList2.add(data.toFloat())
        valueList3.add(YPoint(index.intValue.toFloat(), data.toFloat()))
        Log.d("TAG", valueList2.toList().toString())
        index.intValue++
    }

    //Ycharts
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Blue)
        .steps(valueList3.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(20)
        .backgroundColor(Color.Red)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            i.toString()
        }.build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = valueList3,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )

    val chartEntryModel = entryModelOf(valueList)
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = data.toString())
        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            startAxis = rememberStartAxis(
                title = "Pls show stuff",
                tickLength = 0.dp,
                valueFormatter = { value, _ -> (value.toString()) }),
            bottomAxis = rememberBottomAxis(
                title = "Pls show stuff",
                tickLength = 0.dp,
            ),
        )
        if (valueList2.toList().size > 2) {
            LineChartView(ChartDataSet(valueList2.toList(), title = "Data"))
            LineChart(modifier = Modifier.fillMaxSize(), lineChartData = lineChartData)

        }
    }
}