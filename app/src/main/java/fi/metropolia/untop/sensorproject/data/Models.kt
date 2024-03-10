package fi.metropolia.untop.sensorproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    val date: String,
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val illuminance: Double
)

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey
    val name: String,
    val description: String,
    var currentValue: Boolean,
)