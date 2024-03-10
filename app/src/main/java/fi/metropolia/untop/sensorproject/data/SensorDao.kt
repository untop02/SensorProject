package fi.metropolia.untop.sensorproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)
    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getALL(): List<Item>

    //Tietystä ajasta tiettyyn
    //@Query("SELECT * FROM items WHERE ")
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(setting: Setting)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSettings(settings: List<Setting>)

    @Update
    suspend fun update(setting: Setting)
    @Query("UPDATE settings SET currentValue = :newValue WHERE name = :name")
    suspend fun updateValue(name: String, newValue: Boolean)
    @Query("SELECT * FROM settings WHERE name = :name")
    suspend fun getSetting(name: String): Setting

    @Query("SELECT * FROM settings")
    suspend fun getALL(): List<Setting>
}