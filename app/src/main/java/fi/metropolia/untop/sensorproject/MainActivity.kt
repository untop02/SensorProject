package fi.metropolia.untop.sensorproject

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fi.metropolia.untop.sensorproject.ui.theme.SensorProjectTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private val viewModel: MyViewModel by viewModels()
    private lateinit var mSensorManager: SensorManager
    private var mTemp: Sensor? = null
    private var mLight: Sensor? = null
    private var mPressure: Sensor? = null
    private var mHumidity: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        /*
                viewModel.makeTestData(viewModel.ambientTemp)
                viewModel.makeTestData(viewModel.humidity)
                viewModel.makeTestData(viewModel.light)
                viewModel.makeTestData(viewModel.pressure)
        */
        val requiredPermissions: Array<String> = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        val permissionsGranted = HashMap<String, Boolean>()
        val requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach { entry ->
                    if (entry.value) {
                        permissionsGranted[entry.key] = entry.value
                        Log.d("PermissionGranted", "Permission ${entry.key} is granted")
                    } else {
                        Log.d("PermissionDenied", "Permission ${entry.key} is denied")
                    }
                }
            }
        requestPermissionsLauncher.launch(requiredPermissions)
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
                                Home(modifier = Modifier, viewModel, navController)
                            }
                            composable(Destinations.History.route) {
                                History(modifier = Modifier)
                            }
                            composable(Destinations.Settings.route) {
                                Settings(modifier = Modifier)
                            }
                            composable(
                                Destinations.Graph.route.plus("?observedName={observedName}"),
                                arguments = listOf(
                                    navArgument("observedName") { defaultValue = "null" })
                            ) {
                                Graph(
                                    modifier = Modifier,
                                    viewModel,
                                    it.arguments?.getString("observedName")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> viewModel.ambientTemp.postValue(event.values[0].toDouble())
            Sensor.TYPE_LIGHT -> viewModel.light.postValue(event.values[0].toDouble())
            Sensor.TYPE_PRESSURE -> viewModel.pressure.postValue(event.values[0].toDouble())
            Sensor.TYPE_RELATIVE_HUMIDITY -> viewModel.humidity.postValue(event.values[0].toDouble())
        }
    }
}