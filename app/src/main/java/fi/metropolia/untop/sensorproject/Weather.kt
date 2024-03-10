package fi.metropolia.untop.sensorproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fi.metropolia.untop.sensorproject.data.MyViewModel

private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

@Composable
fun Weather(modifier: Modifier, context: Context, viewModel: MyViewModel) {

    Spacer(modifier = Modifier.height(20.dp))
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 5.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Outside:")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(100.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = viewModel.weatherData.value?.main?.temp.toString(),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.weatherData.value?.main?.humidity.toString(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.weatherData.value?.main?.pressure.toString(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


            }
        ElevatedButton(onClick = {
            getLocation(
                context = context,
                onGetLastLocationSuccess = { location ->
                    val (lat, long) = location
                    Log.d("DBG", "THIS SHOULD BE LAT AND LONG $lat $long")
                    viewModel.getWeather(lat,long)
                },
                onGetLastLocationFailed = { exception ->
                    Log.e("Location", "Failed to retrieve location: ${exception.message}")
                })
        }) {
            Text(text = "Press")
        }
    }
}

private fun getLocation(
    context: Context,
    onGetLastLocationSuccess: (Pair<Double, Double>) -> Unit,
    onGetLastLocationFailed: (Exception) -> Unit
) {
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("DBG", "Why no location")
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location ->
            location?.let {
                onGetLastLocationSuccess(Pair(it.latitude, it.longitude))
            }
        }
        .addOnFailureListener { exception ->
            onGetLastLocationFailed(exception)
        }
}