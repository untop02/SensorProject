package fi.metropolia.untop.sensorproject.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.untop.sensorproject.api.RetrofitInstance
import fi.metropolia.untop.sensorproject.api.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.ThreadLocalRandom

class MyViewModel(private val sensorRepository: SensorRepository) : ViewModel() {
    private val repository: WeatherRepository = WeatherRepository()
    val ambientTemp = MutableLiveData(0.0)
    val humidity = MutableLiveData(0.0)
    val light = MutableLiveData(0.0)
    val pressure = MutableLiveData(0.0)
    val weatherData = MutableLiveData<WeatherResponse>()
    var history = MutableLiveData<List<Item>>(emptyList())
    var currentSettings = MutableLiveData<List<Setting>>(emptyList())
    var theme = MutableLiveData(false)
    private var automatic = MutableLiveData(true)
    var isNightMode = MutableLiveData(false)
    var nullSensors = MutableLiveData<List<String>>(emptyList())

    class WeatherRepository {
        suspend fun getWeather(lat: Double, long: Double): WeatherResponse {
            return RetrofitInstance.service.getWeather(lat = lat, lon = long)
        }
    }

    fun getWeather(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serverResp = repository.getWeather(lat, long)
                weatherData.postValue(serverResp)
            } catch (e: Exception) {
                println(e.stackTrace)
            }
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

    fun getAllSettings() {
        viewModelScope.launch {
            try {
                val settings = sensorRepository.getAllSettings()
                updateSettings(settings)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error getting settings: ${e.message}")
            }
        }
    }

    private fun updateSettings(settings: List<Setting>) {
        automatic.postValue(settings[0].currentValue)
        val themeValue = if (settings[0].currentValue) {
            isNightMode.value
        } else {
            settings[1].currentValue
        }
        theme.postValue(themeValue)
        if (settings[0].currentValue) {
            settings[1].currentValue = true
            try {
                updateSettingValue("Theme", true)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error Updating setting: ${e.message}")
            }
        }
        currentSettings.postValue(settings)
    }

    fun makeTestData(testData: MutableLiveData<Double>) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val newNumber = ThreadLocalRandom.current().nextDouble(0.0, 101.0)
                testData.postValue(newNumber)
                sleep(1000)
            }
        }
    }
}