package ar.edu.utn.frba.homeassistant.data.repository

import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val deviceDao: DeviceDao,
    private val sceneDao: SceneDao
) {
    fun getDevices() = deviceDao.getAll()

    suspend fun addDevice(name: String, type: String) =
        deviceDao.insert(Device(name = name, type = type))

    suspend fun deleteDevice(device: Device) = deviceDao.delete(device)

    fun getScenesWithDevices() = sceneDao.getAllWithDevices()

    suspend fun addScene(name: String, devices: List<Device>) {
        val sceneId = sceneDao.insert(Scene(name = name))
        devices.forEach {
            sceneDao.insertSceneDeviceCrossRef(
                SceneDeviceCrossRef(
                    sceneId,
                    it.deviceId
                )
            )
        }
    }

    suspend fun deleteScene(scene: Scene) = sceneDao.delete(scene)

    suspend fun updateScene(sceneId: Long, devices: List<Device>) {
        sceneDao.deleteSceneDevices(sceneId)
        devices.forEach {
            sceneDao.insertSceneDeviceCrossRef(
                SceneDeviceCrossRef(
                    sceneId,
                    it.deviceId
                )
            )
        }
    }
}