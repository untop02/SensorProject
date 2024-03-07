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
                // Insert item in a transaction
                sensorRepository.insertItem(item)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error inserting item: ${e.message}")
            }
            getAll()
        }
    }


    fun updateItem(item: Item) {
        viewModelScope.launch {
            sensorRepository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            sensorRepository.deleteItem(item)
        }
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                val data = sensorRepository.getAll()
                history.postValue(data)
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error getting items: ${e.message}")
            }
        }
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