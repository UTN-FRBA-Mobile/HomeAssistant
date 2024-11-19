package ar.edu.utn.frba.homeassistant.data.repository

import androidx.lifecycle.LiveData
import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val deviceDao: DeviceDao,
    private val sceneDao: SceneDao,
    private val automationDao: AutomationDao
) {
    fun getDevices() = deviceDao.getAll()

    suspend fun getDeviceById(id: Long) = deviceDao.getById(id)

    suspend fun addDevice(id: Long, name: String, type: String) =
        deviceDao.insert(Device(deviceId = id, name = name, type = type))

    suspend fun updateDevice(device: Device) = deviceDao.update(device)

    suspend fun deleteDevice(device: Device) = deviceDao.delete(device)

    fun getScenesWithDevices() = sceneDao.getAllWithDevices()

    fun getScenes() = sceneDao.getAll();

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

    fun getClockAutomationsWithScenes() = automationDao.getAllClockWithScenes()
    fun getGeolocationAutomationsWithScenes() = automationDao.getAllGeolocationWithScenes()

    private suspend fun insertAutomationScenesCrossRef(id: Long, automationWithScenes: IAutomationWithScenes) {
        automationWithScenes.scenes.forEach {
            println(it)
            automationDao.insertAutomationSceneCrossRef(
                ar.edu.utn.frba.homeassistant.data.model.AutomationSceneCrossRef(
                    id, //
                    it.sceneId
                )
            )
        }
    }

    suspend fun addAutomation(clockAutomationWithScenes: ClockAutomationWithScenes) {
        println(clockAutomationWithScenes)
        val id = automationDao.insert(clockAutomationWithScenes.automation)
        insertAutomationScenesCrossRef(id, clockAutomationWithScenes)
    }

    suspend fun addAutomation(geolocationAutomationWithScenes: GeolocationAutomationWithScenes) {
        println(geolocationAutomationWithScenes)
        val id = automationDao.insert(geolocationAutomationWithScenes.automation)
        insertAutomationScenesCrossRef(id, geolocationAutomationWithScenes)
    }
}