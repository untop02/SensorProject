package fi.metropolia.untop.sensorproject.data


interface SensorRepository {

    suspend fun insertItem(item: Item)

    suspend fun getAllItems(): List<Item>

    suspend fun insertAllSettings(settings: List<Setting>)

    suspend fun updateSettingValue(name: String, newValue: Boolean) {
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

    override suspend fun getAllItems(): List<Item> {
        return sensorDao.getALL()
    }

    override suspend fun insertAllSettings(settings: List<Setting>) =
        settingsDao.insertAllSettings(settings)

    override suspend fun updateSetting(setting: Setting) = settingsDao.update(setting)

    override suspend fun updateSettingValue(name: String, newValue: Boolean) =
        settingsDao.updateValue(name, newValue)

    override suspend fun getSetting(name: String): Setting {
        return settingsDao.getSetting(name)
    }

    override suspend fun getAllSettings(): List<Setting> {
        return settingsDao.getALL()
    }
}