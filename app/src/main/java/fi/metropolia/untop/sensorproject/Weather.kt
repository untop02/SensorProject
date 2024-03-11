package fi.metropolia.untop.sensorproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fi.metropolia.untop.sensorproject.data.MyViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

@Composable
fun Weather(modifier: Modifier, context: Context, viewModel: MyViewModel) {

    Spacer(modifier = Modifier.height(20.dp))
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
    Column(
        modifier = modifier
                .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Log.d("Weather", viewModel.weatherData.observeAsState().value.toString())
        Text(text = "Outside",
                modifier = modifier
                .fillMaxWidth()
            .padding(16.dp),
            fontSize = 32.sp, // Larger font size for title
            color = Color.DarkGray, // Customize color as needed
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                ApiCard(
                    modifier = modifier,
                    name = "Temperature",
                    value = viewModel.weatherData.value?.main?.temp.toString(),
                    unit = " °C"
                    )
                ApiCard(
                    modifier = modifier,
                    name = "Humidity",
                    value = viewModel.weatherData.value?.main?.humidity.toString(),
                    unit = " %"
                )



            }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
        )  {
            ApiCard(
                modifier = modifier,
                name = "Ambient air pressure",
                value = viewModel.weatherData.value?.main?.pressure.toString(),
                unit = " hPa"
            )
            ApiCard(
                modifier = modifier,
                name = "Feels like",
                value = viewModel.weatherData.value?.main?.feels_like.toString(),
                unit = " °C"
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

@Composable
fun ApiCard(
    modifier: Modifier,
    name: String,
    value: String,
    unit: String
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .width(170.dp)
    ) {
        Text(
            text = name,
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value + unit, modifier = modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally), textAlign = TextAlign.Center
        )
    }
}
fun getLocation(
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
