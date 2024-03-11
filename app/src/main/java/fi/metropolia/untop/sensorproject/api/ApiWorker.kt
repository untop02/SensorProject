package fi.metropolia.untop.sensorproject.api

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class ApiWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val data = MutableLiveData("")
    override suspend fun doWork(): Result {
        return try {
            getData().wait()
            Log.d("DBG", "DOES THIS SHIT HAPPEN EARLY")
            val results = Data.Builder().putString("modified_data", data.value).build()
            Result.success(results)
        } catch (e: Exception) {
            Log.d("DBG", "DOES THIS SHIT HAPPEN EARLY")
            Result.failure()
        }
    }

    private suspend fun getData() {
        val myCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        getLocation(context = this.applicationContext, onGetLastLocationSuccess = { location ->
            val (lat, long) = location
            myCoroutineScope.launch {
                val serverResp = RetrofitInstance.service.getWeather(lat, long)
                Log.d("DBG", "Weather data between $serverResp")
                val gson = Gson()
                val modifiedJson = gson.toJson(serverResp)
                data.postValue(modifiedJson)
                Log.d("DBG", "Weather data after $modifiedJson")
            }
        }, onGetLastLocationFailed = { exception ->
            Log.e("Location", "Failed to retrieve location: ${exception.message}")
        })
    }

    private fun getLocation(
        context: Context,
        onGetLastLocationSuccess: (Pair<Double, Double>) -> Unit,
        onGetLastLocationFailed: (Exception) -> Unit
    ) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DBG", "Why no location")
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onGetLastLocationSuccess(Pair(it.latitude, it.longitude))
            }
        }.addOnFailureListener { exception ->
            onGetLastLocationFailed(exception)
        }
    }

}