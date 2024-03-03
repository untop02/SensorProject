package fi.metropolia.untop.sensorproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val ambientTemp = MutableLiveData(0.0)
    val humidity = MutableLiveData(0.0)
    val light = MutableLiveData(0.0)
    val pressure = MutableLiveData(0.0)

}