package fi.metropolia.untop.sensorproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.ThreadLocalRandom

class MyViewModel : ViewModel() {
    val test1Data = MutableLiveData(0)
    val test2Data = MutableLiveData(0)
    val test3Data = MutableLiveData(0)
    val test4Data = MutableLiveData(0)

    fun makeTestData(testData: MutableLiveData<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val newNumber = ThreadLocalRandom.current().nextInt(0, 100)
                testData.postValue(newNumber)
                sleep(1000)
            }
        }
    }
}