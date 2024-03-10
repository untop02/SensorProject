package fi.metropolia.untop.sensorproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.metropolia.untop.sensorproject.data.MyViewModel
import fi.metropolia.untop.sensorproject.data.OfflineRepo
import fi.metropolia.untop.sensorproject.data.SensorDatabase
import fi.metropolia.untop.sensorproject.data.Setting

@Composable
fun Settings(viewModel: MyViewModel) {
    val settingsState by viewModel.currentSettings.observeAsState()

    settingsState?.let { settings ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
        ) {
            items(settings) { setting ->
                SettingItem(
                    modifier = Modifier.padding(5.dp),
                    setting = setting,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SettingItem(modifier: Modifier, setting: Setting, viewModel: MyViewModel) {
    val settingValues = when (setting.name) {
        "Automatic" -> Pair(
            stringResource(id = R.string.setting_name_automatic),
            stringResource(id = R.string.setting_desc_automatic)
        )

        "Theme" -> Pair(
            stringResource(id = R.string.setting_name_theme),
            stringResource(id = R.string.setting_desc_theme)
        )

        else -> {
            Pair(
                stringResource(id = R.string.setting_name_language),
                stringResource(id = R.string.setting_desc_language)
            )
        }
    }
    var checked by rememberSaveable { mutableStateOf(setting.currentValue) }
    ElevatedCard(
        Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            Modifier.clip(shape = RoundedCornerShape(500.dp))
        ) {
            Image(
                imageVector = Icons.Rounded.Build,
                contentDescription = "Setting",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .padding(8.dp)
                    .width(50.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
            Column(
                modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)
            ) {
                Text(
                    text = settingValues.first,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = settingValues.second
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    whatToChange(viewModel, setting.name, it)
                },
                modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp),
                thumbContent = {
                    if (checked) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    }
}

fun whatToChange(viewModel: MyViewModel, name: String, currentValue: Boolean) {
    viewModel.updateSettingValue(name, currentValue)
    if (name == "Theme") {
        viewModel.theme.value = currentValue
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPrev() {
    val context = LocalContext.current
    val sensorDatabase = SensorDatabase.getDatabase(context)
    Settings(
        viewModel = MyViewModel(
            OfflineRepo(
                sensorDatabase.itemDao(),
                sensorDatabase.settingsDao()
            )
        )
    )
}