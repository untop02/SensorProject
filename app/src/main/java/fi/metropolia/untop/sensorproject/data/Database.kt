package fi.metropolia.untop.sensorproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Item::class], version = 1, exportSchema = false)
@TypeConverters(LocalTimeConverter::class)
abstract class SensorDatabase : RoomDatabase() {

    abstract fun itemDao(): SensorDao

    companion object {
        @Volatile
        private var Instance: SensorDatabase? = null

        fun getDatabase(context: Context): SensorDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SensorDatabase::class.java, "sensor_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}