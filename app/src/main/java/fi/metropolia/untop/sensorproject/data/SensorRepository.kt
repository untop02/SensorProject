package fi.metropolia.untop.sensorproject.data


interface SensorRepository {

    suspend fun insertItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun deleteItem(item: Item)

    suspend fun getAllItems(): List<Item>
    suspend fun insertSetting(setting: Setting)

    suspend fun insertAllSettings(settings: List<Setting>)
    suspend fun updateValue(name: String, newValue: Boolean) {
        val existingSetting = getSetting(name)
        existingSetting.currentValue = newValue
        updateSetting(existingSetting)
    }

    suspend fun updateSetting(setting: Setting)
    suspend fun getSetting(name: String): Setting

    suspend fun getAllSettings(): List<Setting>
}

class OfflineRepo(private val sensorDao: SensorDao, private val settingsDao: SettingsDao) :
    SensorRepository {
    override suspend fun insertItem(item: Item) = sensorDao.insert(item)

    override suspend fun updateItem(item: Item) = sensorDao.update(item)

    override suspend fun deleteItem(item: Item) = sensorDao.delete(item)

    override suspend fun getAllItems(): List<Item> {
        return sensorDao.getALL()
    }

    override suspend fun insertSetting(setting: Setting) = settingsDao.insert(setting)

    override suspend fun insertAllSettings(settings: List<Setting>) =
        settingsDao.insertAllSettings(settings)

    override suspend fun updateSetting(setting: Setting) = settingsDao.update(setting)

    override suspend fun updateValue(name: String, newValue: Boolean) =
        settingsDao.updateValue(name, newValue)

    override suspend fun getSetting(name: String): Setting {
        return settingsDao.getSetting(name)
    }

    override suspend fun getAllSettings(): List<Setting> {
        return settingsDao.getALL()
    }
}