package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.untop.sensorproject.data.MyViewModel
import fi.metropolia.untop.sensorproject.data.OfflineRepo
import fi.metropolia.untop.sensorproject.data.SensorDatabase
import fi.metropolia.untop.sensorproject.graphs.Graph

@Composable
fun History(modifier: Modifier, viewModel: MyViewModel) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(id = R.string.history_name),
                modifier.padding(16.dp),
                fontFamily = FontFamily.Cursive,
                fontSize = 70.sp,
            )
            if (viewModel.history.value?.isNotEmpty() == true) {
                Graph(modifier = modifier, viewModel = viewModel, name = null)
            } else {
                Text(stringResource(R.string.history_no_data))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListPrev() {
    val context = LocalContext.current
    val sensorDatabase = SensorDatabase.getDatabase(context)
    History(
        modifier = Modifier,
        viewModel = MyViewModel(
            OfflineRepo(
                sensorDatabase.itemDao(),
                sensorDatabase.settingsDao()
            )
        ),
    )
}
