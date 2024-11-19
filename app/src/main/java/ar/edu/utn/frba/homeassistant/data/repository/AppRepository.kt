package ar.edu.utn.frba.homeassistant.data.repository

import androidx.lifecycle.LiveData
import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationWithScenes
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
    fun getShakeAutomationsWithScenes() = automationDao.getAllShakeWithScenes()


    suspend fun addAutomation(clockAutomationWithScenes: ClockAutomationWithScenes): Long {
        println(clockAutomationWithScenes)
        val inserted = automationDao.insert(clockAutomationWithScenes.automation)
        println(inserted)
        clockAutomationWithScenes.scenes.forEach {
            println(it)
            automationDao.insertAutomationSceneCrossRef(
                ClockAutomationSceneCrossRef(
                    it.sceneId,
                    inserted //
                )
            )
        }
        return inserted;
    }

    suspend fun addAutomation(geolocationAutomationWithScenes: GeolocationAutomationWithScenes): Long {
        println(geolocationAutomationWithScenes)
        val id = automationDao.insert(geolocationAutomationWithScenes.automation)
        geolocationAutomationWithScenes.scenes.forEach {
            println(it)
            automationDao.insertAutomationSceneCrossRef(
                GeolocationAutomationSceneCrossRef(
                    it.sceneId, id
                )
            )
        }
        return id;
    }

    suspend fun addAutomation(shakeAutomationWithScenes: ShakeAutomationWithScenes): Long {
        println(shakeAutomationWithScenes)
        val id = automationDao.insert(shakeAutomationWithScenes.automation)
        shakeAutomationWithScenes.scenes.forEach {
            println(it)
            automationDao.insertAutomationSceneCrossRef(
                ShakeAutomationSceneCrossRef(
                    it.sceneId,
                    id
                )
            )
        }
        return id
    }

    suspend fun updateAutomation(automation: ClockAutomation){
        automationDao.update(automation)
    }

    suspend fun updateAutomation(automation: GeolocationAutomation){
        automationDao.update(automation)
    }

    suspend fun updateAutomation(automation: ShakeAutomation){
        automationDao.update(automation)
    }
}