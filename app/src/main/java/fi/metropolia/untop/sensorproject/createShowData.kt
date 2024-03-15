package fi.metropolia.untop.sensorproject

import fi.metropolia.untop.sensorproject.data.Item
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun createMockData(): List<Item> {
    val currentDate = LocalDate.now()
    val twoMonthsAgo = currentDate.minusMonths(1)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val items = mutableListOf<Item>()

    for (i in 1..60) {
        val date = twoMonthsAgo.plusDays(i.toLong()).format(formatter)
        val temperature = generateRandomTemperature()
        val humidity = generateRandomHumidity()
        val pressure = generateRandomPressure()
        val illuminance = generateRandomIlluminance()
        val temperatureAPI = generateRandomTemperatureAPI()
        val humidityAPI = generateRandomHumidityAPI()
        val pressureAPI = generateRandomPressureAPI()

        items.add(
            Item(
                date,
                temperature,
                humidity,
                pressure,
                illuminance,
                temperatureAPI,
                humidityAPI,
                pressureAPI
            )
        )
    }

    return items
}

fun generateRandomTemperature(): Double {
    return (Math.random() * 50) + 10
}

fun generateRandomHumidity(): Double {
    return (Math.random() * 90) + 10
}


fun generateRandomPressure(): Double {
    return (Math.random() * 1000) + 900
}


fun generateRandomIlluminance(): Double {
    return (Math.random() * 10000) + 1000
}


fun generateRandomTemperatureAPI(): Double {
    return (Math.random() * 50) + 10
}


fun generateRandomHumidityAPI(): Double {
    return (Math.random() * 90) + 10
}


fun generateRandomPressureAPI(): Double {
    return (Math.random() * 1000) + 900
}