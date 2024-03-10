package fi.metropolia.untop.sensorproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class, Setting::class], version = 1, exportSchema = false)
abstract class SensorDatabase : RoomDatabase() {

    abstract fun itemDao(): SensorDao
    abstract fun settingsDao(): SettingsDao

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