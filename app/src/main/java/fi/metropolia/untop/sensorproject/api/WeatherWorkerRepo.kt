package fi.metropolia.untop.sensorproject.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object WeatherWorkerRepo {
    private val data = MutableLiveData<WeatherResponse>()

    fun getData(): LiveData<WeatherResponse> {
        return data
    }

    fun updateData(newData: WeatherResponse) {
        data.postValue(newData)
    }
}