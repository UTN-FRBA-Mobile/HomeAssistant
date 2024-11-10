package ar.edu.utn.frba.homeassistant.data.repository

import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.model.Device
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val deviceDao: DeviceDao
) {
    fun getDevices() = deviceDao.getAll()

    suspend fun addDevice(name: String, type: String) =
        deviceDao.insert(Device(name = name, type = type))

    suspend fun deleteDevice(device: Device) = deviceDao.delete(device)
}