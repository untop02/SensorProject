package fi.metropolia.untop.sensorproject.data

import android.Manifest
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.untop.sensorproject.api.WeatherResponse
import fi.metropolia.untop.sensorproject.api.WeatherWorkerRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyViewModel(private val sensorRepository: SensorRepository) : ViewModel() {
    //Sensordata
    val ambientTemp = MutableLiveData(0.0)
    val humidity = MutableLiveData(0.0)
    val light = MutableLiveData(0.0)
    val pressure = MutableLiveData(0.0)
    val weatherData: LiveData<WeatherResponse> = WeatherWorkerRepo.getData()

    //History
    var history = MutableLiveData<List<Item>>(emptyList())

    //Settings
    var currentSettings = MutableLiveData<List<Setting>>(emptyList())

    //Bluetooth
    private val mResults = HashMap<String, ScanResult>()
    val scanResults = MutableLiveData<List<ScanResult>>(null)
    var theme = MutableLiveData<Boolean>()

    //Theme
    private var automatic = MutableLiveData(true)
    var isNightMode = MutableLiveData(false)

    //Sensors
    var nullSensors = MutableLiveData<List<String>>(emptyList())

    //Gatt
    val connectionState = MutableLiveData("")
    fun scanDevices(scanner: BluetoothLeScanner, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@launch
            }
            scanner.startScan(null, settings, leScanCallback)
            delay(SCAN_PERIOD)
            scanner.stopScan(leScanCallback)
            scanResults.postValue(mResults.values.toList().sortedBy { it.rssi }.asReversed())
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceAddress = device.address
            mResults[deviceAddress] = result
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            try {
                sensorRepository.insertItem(item)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error inserting item: ${e.message}")
            }
            getAllItems()
        }
    }

    fun getAllItems() {
        viewModelScope.launch {
            try {
                val data = sensorRepository.getAllItems()
                history.postValue(data)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error getting items: ${e.message}")
            }
        }
    }

    fun insertAllSettings(settings: List<Setting>) {
        Log.d("DBG", settings.toString())
        viewModelScope.launch {
            try {
                sensorRepository.insertAllSettings(settings)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error inserting settings: ${e.message}")

            }
        }
    }

    fun updateSettingValue(name: String, newValue: Boolean) {
        viewModelScope.launch {
            try {
                sensorRepository.updateSettingValue(name, newValue)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error updating setting ${e.message}")
            }
        }
    }

    suspend fun getAllSettings(): List<Setting> {
        return try {
            sensorRepository.getAllSettings()
        } catch (e: Exception) {
            Log.e("MyViewModel", "Error getting settings: ${e.message}")
            emptyList()
        }
    }

    fun updateSettings(settings: List<Setting>) {
        val automaticValue = settings.getOrNull(0)?.currentValue ?: true
        val currentThemeValue = settings.getOrNull(1)?.currentValue ?: true
        automatic.postValue(automaticValue)

        val themeValue = if (automaticValue) {
            isNightMode.value
        } else {
            currentThemeValue
        }

        themeValue?.let { newValue ->
            theme.postValue(newValue)
            if (automaticValue && !newValue) {
                updateSettingValue("Theme", true)
            }
        }
        currentSettings.postValue(settings)
    }

    fun saveSenorsToDatabase() {
        val newItem = Item(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            ambientTemp.value ?: 0.0,
            humidity.value ?: 0.0,
            pressure.value ?: 0.0,
            light.value ?: 0.0,
            weatherData.value?.main?.temp ?: 0.0,
            weatherData.value?.main?.humidity?.toDouble() ?: 0.0,
            weatherData.value?.main?.pressure?.toDouble() ?: 0.0,
        )
        insertItem(newItem)
    }

    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 5000
    }
}