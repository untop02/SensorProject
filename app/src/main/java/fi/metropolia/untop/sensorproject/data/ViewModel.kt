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
                Log.d("DBG", serverResp.toString())
                weatherData.postValue(serverResp)

            } catch (e: Exception) {
                println(e.stackTrace)
            }
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            Log.d("DBG", "Before insertItem")
            sensorRepository.insertItem(item)
            Log.d("DBG", "After insertItem")
            sensorRepository.getAll()
            Log.d("DBG", "After getAll")
        }
    }


    fun updateItem(item: Item) {
        viewModelScope.launch {
            sensorRepository.updateItem(item)
            sensorRepository.getAll()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            sensorRepository.deleteItem(item)
            sensorRepository.getAll()
        }
    }

    fun getAll() {
        Log.d("DBG", "Starting getALl")
        viewModelScope.launch {
            // Fetch all items from the repository
            history.value = sensorRepository.getAll().toMutableList()

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