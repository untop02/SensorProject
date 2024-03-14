package fi.metropolia.untop.sensorproject.api

import fi.metropolia.untop.sensorproject.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object RetrofitInstance {
    //https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=7e110d7bc0a43143d9e7eff08e3bba31&units=metric
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val APIKEY = BuildConfig.API_KEY

    interface WeatherService {
        @GET("weather")
        suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String = APIKEY,
            @Query("units") units: String = "metric"
        ): WeatherResponse
    }


    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val rain: Rain,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

data class Clouds(
    val all: Int
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Rain(
    val `1h`: Double
)

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)