package fi.metropolia.untop.sensorproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalTime

@Entity(tableName = "items")
data class Item(
    @PrimaryKey()
    val date: String,
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val illuminance: Double
)
class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(value: LocalTime): String {
        return value.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String): LocalTime {
        return LocalTime.parse(value)
    }
}