package fi.metropolia.untop.sensorproject.api

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class ApiWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override suspend fun doWork(): Result {
        return try {
            Log.d("DBG", "Started worker")
            val gson = Gson()
            val myData = gson.fromJson(
                weatherData(getLocation(applicationContext)), WeatherResponse::class.java
            )
            WeatherWorkerRepo.updateData(myData)

            Result.success()
        } catch (e: Exception) {
            Log.d("DBG", "CRASHED WORKER")
            Result.failure()
        }
    }

    private suspend fun weatherData(location: Pair<Double, Double>?): String? {
        Log.d("DBG", "Started worker weather fetch")
        if (location != null) {
            val serverResp = RetrofitInstance.service.getWeather(location.first, location.second)
            val gson = Gson()
            Log.d("DBG", "Worker serverResp $serverResp")
            return gson.toJson(serverResp)
        }
        return null
    }

    private suspend fun getLocation(
        context: Context
    ): Pair<Double, Double>? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DBG", "Why no location, sending null")
            return null
        }
        return try {
            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                Log.d("DBG", "Success location $location")
                Pair(location.latitude, location.longitude)
            } else {
                Log.d("DBG", "Last location is null")
                null
            }
        } catch (e: Exception) {
            Log.e("DBG", "Error getting last location: ${e.message}")
            null
        }
    }

}