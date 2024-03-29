package fi.metropolia.untop.sensorproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fi.metropolia.untop.sensorproject.api.ApiWorker
import fi.metropolia.untop.sensorproject.data.MyViewModel
import fi.metropolia.untop.sensorproject.data.OfflineRepo
import fi.metropolia.untop.sensorproject.data.SensorDatabase
import fi.metropolia.untop.sensorproject.data.Setting
import fi.metropolia.untop.sensorproject.graphs.Graph
import fi.metropolia.untop.sensorproject.ui.theme.SensorProjectTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mTemp: Sensor? = null
    private var mLight: Sensor? = null
    private var mPressure: Sensor? = null
    private var mHumidity: Sensor? = null
    private lateinit var database: SensorDatabase
    private lateinit var viewModel: MyViewModel
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var permissionsGranted: HashMap<String, Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requiredPermissions: Array<String> = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        permissionsGranted = HashMap()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        getPermissions(requiredPermissions)
        initializeViewModelAndDatabase()
        initializeSensors()
        insertMockData()
        viewModel.weatherData.observe(this) {
            viewModel.saveSenorsToDatabase()
        }
        setContent {
            val theme by viewModel.theme.observeAsState(true)
            val navController = rememberNavController()
            var navigationSelectedItem by rememberSaveable { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                val settings = viewModel.getAllSettings()
                viewModel.updateSettings(settings)
                viewModel.getAllItems()
            }
            theme?.let { it ->
                SensorProjectTheme(darkTheme = it) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Scaffold(
                            bottomBar = {
                                BottomAppBar(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                ) {
                                    BottomNavigationItem().bottomNavigationItems()
                                        .forEachIndexed { index, navigationItem ->
                                            NavigationBarItem(selected = index == navigationSelectedItem,
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
                                                })
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
                                    if (bluetoothAdapter != null) {
                                        Home(
                                            modifier = Modifier,
                                            viewModel,
                                            navController,
                                            permissionsGranted,
                                            bluetoothAdapter,
                                            requestPermissionsLauncher,
                                            requiredPermissions,
                                        )
                                    }
                                }
                                composable(Destinations.History.route) {
                                    History(modifier = Modifier, viewModel)
                                }
                                composable(Destinations.Settings.route) {
                                    Settings(viewModel)
                                }
                                composable(
                                    Destinations.Graph.route.plus("?observedName={observedName}"),
                                    arguments = listOf(navArgument("observedName") {
                                        defaultValue = "null"
                                    })
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
    }

    private fun insertMockData() {
        val list = createMockData()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                list.forEach { item ->
                    viewModel.insertItem(item)
                }
            }
        }
    }

    private fun initializeViewModelAndDatabase() {
        database = SensorDatabase.getDatabase(this)
        viewModel = MyViewModel(OfflineRepo(database.itemDao(), database.settingsDao()))
        insertSettings()
    }

    private fun insertSettings() {
        val nightMode = isNightModeEnabled()
        viewModel.isNightMode.postValue(nightMode)
        val settings = createSettingsList(nightMode)
        viewModel.insertAllSettings(settings)
    }

    private fun createSettingsList(nightMode: Boolean): List<Setting> {
        return listOf(
            Setting(
                name = "Automatic",
                description = "Automatically change theme",
                currentValue = true
            ),
            Setting(
                name = "Theme",
                description = "Change application theme",
                currentValue = nightMode
            ),
        )
    }

    private fun isNightModeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun getPermissions(
        requiredPermissions: Array<String>
    ) {
        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach { entry ->
                    if (entry.value) {
                        permissionsGranted[entry.key] = entry.value
                        Log.d("DBG", "Permission ${entry.key} is granted")
                    } else {
                        Log.d("DBG", "Permission ${entry.key} is denied")
                    }
                }
                if (permissionsGranted.contains("android.permission.ACCESS_FINE_LOCATION")
                    && permissionsGranted.contains("android.permission.ACCESS_COARSE_LOCATION")
                ) {
                    initializeWorkers()
                }
            }

        requestPermissionsLauncher.launch(requiredPermissions)
    }


    private fun initializeWorkers() {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<ApiWorker>(20, TimeUnit.MINUTES).build()
        val initialWorkRequest = OneTimeWorkRequestBuilder<ApiWorker>().build()
        WorkManager.getInstance(this).enqueue(initialWorkRequest)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "interval_check", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest
        )
    }

    //starting the phones internal sensors
    private fun initializeSensors() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        mTemp = mSensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mLight = mSensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        mPressure = mSensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)
        mHumidity = mSensorManager?.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        val sensors = mapOf(
            Sensor.TYPE_AMBIENT_TEMPERATURE to "Temperature",
            Sensor.TYPE_LIGHT to "Light",
            Sensor.TYPE_PRESSURE to "Pressure",
            Sensor.TYPE_RELATIVE_HUMIDITY to "Humidity"
        )

        val nullSensors = sensors.filter { (sensorType, _) ->
            mSensorManager?.getDefaultSensor(sensorType) == null
        }.map { (_, sensorName) -> sensorName }
        viewModel.nullSensors.postValue(nullSensors)
    }

    //continuing to track phones internal sensors when user opens app up again
    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager?.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager?.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager?.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    //stopping the tracking of sensors when exiting the app
    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    //tracking sensors reported data and changing variables when there is a change
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