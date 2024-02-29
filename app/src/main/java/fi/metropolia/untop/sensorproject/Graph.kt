package fi.metropolia.untop.sensorproject

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
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries

@Composable
fun Graph(modifier: Modifier, viewModel: MyViewModel, name: String?) {
    val valueList = remember { mutableStateListOf<Number>() }
    val index = remember { mutableIntStateOf(0) }
    val modelProducer = remember { CartesianChartModelProducer.build() }

    val data = when (name) {
        "Temperature" -> viewModel.test1Data.observeAsState(initial = 0).value
        "Humidity" -> viewModel.test2Data.observeAsState(initial = 0).value
        //Pitää vielä säätää oikein, sitte ko tietää mitä näytetään🐨
        else -> viewModel.test3Data.observeAsState(initial = 0).value
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
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            modelProducer,
            marker = rememberMarker(),
        )
    }


}