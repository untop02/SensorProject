package fi.metropolia.untop.sensorproject

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.metropolia.untop.sensorproject.ui.theme.SensorProjectTheme
class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewModel.makeTestData(viewModel.test1Data)
        viewModel.makeTestData(viewModel.test2Data)
        viewModel.makeTestData(viewModel.test3Data)
        viewModel.makeTestData(viewModel.test4Data)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                val millibarsOfPressure = event.values[0]
                viewModel.pressureData.value = millibarsOfPressure.toInt()
            }
        }, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)

        setContent {
            val navController = rememberNavController()
            var navigationSelectedItem by rememberSaveable { mutableIntStateOf(0) }
            SensorProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            BottomAppBar(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ) {
                                BottomNavigationItem().bottomNavigationItems()
                                    .forEachIndexed { index, navigationItem ->
                                        NavigationBarItem(
                                            selected = index == navigationSelectedItem,
                                            label = {
                                                Text(navigationItem.label)
                                            },
                                            icon = {
                                                Icon(
                                                    navigationItem.icon,
                                                    contentDescription = navigationItem.label
                                                )
                                            },
                                            onClick = {
                                                navigationSelectedItem = index
                                                navController.navigate(navigationItem.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                            }
                        },
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Destinations.Home.route,
                            modifier = Modifier.padding(paddingValues = innerPadding),
                        ) {
                            composable(Destinations.Home.route) {
                                Home(modifier = Modifier, viewModel)
                            }
                            composable(Destinations.History.route) {
                                History(modifier = Modifier)
                            }
                            composable(Destinations.Settings.route) {
                                Settings(modifier = Modifier)
                            }
                        }
                    }
                }
            }
        }
    }
}