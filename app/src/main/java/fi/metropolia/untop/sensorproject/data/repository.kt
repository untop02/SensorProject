package fi.metropolia.untop.sensorproject.data

interface SensorRepository {

    suspend fun insertItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun deleteItem(item: Item)

    suspend fun getAll(): List<Item>
}

class OfflineRepo(private val sensorDao: SensorDao) : SensorRepository {
    override suspend fun insertItem(item: Item) = sensorDao.insert(item)

    override suspend fun updateItem(item: Item) = sensorDao.update(item)

    override suspend fun deleteItem(item: Item) = sensorDao.delete(item)

    override suspend fun getAll(): List<Item> {
        return sensorDao.getALL()
    }
}