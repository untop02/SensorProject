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
import androidx.compose.ui.platform.LocalContext
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.core.component.Component
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.views.chart.line.lineChart

@Composable
fun Graph(modifier: Modifier, viewModel: MyViewModel, name: String?) {
    val context = LocalContext.current
    val valueList = remember { mutableStateListOf<FloatEntry>() }
    val index = remember { mutableIntStateOf(1) }

    val data = when (name) {
        "Temperature" -> viewModel.test1Data.observeAsState(initial = 0).value
        "Humidity" -> viewModel.test2Data.observeAsState(initial = 0).value
        //Pitää vielä säätää oikein, sitte ko tietää mitä näytetään🐨
        else -> viewModel.test3Data.observeAsState(initial = 0).value
    }
    val test = ChartEntryModelProducer(valueList)
    LaunchedEffect(data) {
        valueList.add(FloatEntry(index.intValue.toFloat(), data.toFloat()))
        index.intValue++
    }
    val chartEntryModel = entryModelOf(valueList)
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = data.toString())
        Chart(
            chart = lineChart(context), model = chartEntryModel, startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis()
        )
    }
}

open class MarkerComponent(
    val label: TextComponent,
    val indicator: Component?,
    val guideline: LineComponent?
) : Marker