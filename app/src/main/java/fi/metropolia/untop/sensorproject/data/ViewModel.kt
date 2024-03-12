package fi.metropolia.untop.sensorproject.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.untop.sensorproject.api.WeatherResponse
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyViewModel(private val sensorRepository: SensorRepository) : ViewModel() {
    val ambientTemp = MutableLiveData(0.0)
    val humidity = MutableLiveData(0.0)
    val light = MutableLiveData(0.0)
    val pressure = MutableLiveData(0.0)
    val weatherData = MutableLiveData<WeatherResponse>()
    var history = MutableLiveData<List<Item>>(emptyList())
    var currentSettings = MutableLiveData<List<Setting>>(emptyList())
    var theme = MutableLiveData<Boolean?>()
    private var automatic = MutableLiveData(true)
    var isNightMode = MutableLiveData(false)
    var nullSensors = MutableLiveData<List<String>>(emptyList())

    private fun insertItem(item: Item) {
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

    suspend fun getAllSettings(): List<Setting> {
        return try {
            sensorRepository.getAllSettings()
        } catch (e: Exception) {
            Log.e("MyViewModel", "Error getting settings: ${e.message}")
            emptyList()
        }
    }
    fun updateSettings(settings: List<Setting>) {
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

        Log.d("DBG", "Theme is $themeValue")

        currentSettings.postValue(settings)
    }

    fun saveSenorsToDatabase() {
        val newItem = Item(
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
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
}