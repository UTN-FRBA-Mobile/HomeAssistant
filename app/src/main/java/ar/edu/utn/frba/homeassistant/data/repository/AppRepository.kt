package ar.edu.utn.frba.homeassistant.data.repository

import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationSceneCrossRef
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val deviceDao: DeviceDao,
    private val sceneDao: SceneDao,
    private val automationDao: AutomationDao
) {
    fun getDevices() = deviceDao.getAll()

    suspend fun getDeviceById(id: Long) = deviceDao.getById(id)

    suspend fun getDevicesByIds(ids: List<Long>) = deviceDao.getByIds(ids)

    suspend fun addDevice(id: Long, name: String, type: String) =
        deviceDao.insert(Device(deviceId = id, name = name, type = type))

    suspend fun updateDevice(device: Device) = deviceDao.update(device)

    suspend fun updateIsOn(id: Long, isOn: Boolean) = deviceDao.updateIsOn(id, isOn)

    suspend fun deleteDevice(device: Device) = deviceDao.delete(device)

    fun getScenesWithDevices() = sceneDao.getAllWithDevices()

    fun getScenes() = sceneDao.getAll()

    private suspend fun getScenesWithDevicesByIds(sceneIds: List<Long>) =
        sceneDao.getScenesWithDevicesByIds(sceneIds)

    suspend fun getScenesDevicesIds(sceneIds: List<Long>) =
        getScenesWithDevicesByIds(sceneIds).flatMap { it.devices }.map { it.deviceId }

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

    suspend fun addAutomation(automation: ClockAutomation, scenes: List<Scene>): Long {
        val id = automationDao.insert(automation)
        addClockAutomationScenes(scenes, id)
        return id
    }

    private suspend fun addClockAutomationScenes(scenes: List<Scene>, automationId: Long) {
        scenes.forEach {
            automationDao.insertAutomationSceneCrossRef(
                ClockAutomationSceneCrossRef(
                    it.sceneId,
                    automationId
                )
            )
        }
    }

    suspend fun addAutomation(automation: GeolocationAutomation, scenes: List<Scene>): Long {
        val id = automationDao.insert(automation)
        addGeoAutomationScenes(scenes, id)
        return id
    }

    private suspend fun addGeoAutomationScenes(scenes: List<Scene>, id: Long) {
        scenes.forEach {
            automationDao.insertAutomationSceneCrossRef(
                GeolocationAutomationSceneCrossRef(
                    it.sceneId,
                    id
                )
            )
        }
    }

    suspend fun addAutomation(automation: ShakeAutomation, scenes: List<Scene>): Long {
        val id = automationDao.insert(automation)
        addShakeAutomationScenes(scenes, id)
        return id
    }

    private suspend fun addShakeAutomationScenes(scenes: List<Scene>, id: Long) {
        scenes.forEach {
            automationDao.insertAutomationSceneCrossRef(
                ShakeAutomationSceneCrossRef(
                    it.sceneId,
                    id
                )
            )
        }
    }

    suspend fun updateAutomation(automation: IAutomation) {
        when (automation) {
            is ClockAutomation -> automationDao.update(automation)
            is GeolocationAutomation -> automationDao.update(automation)
            is ShakeAutomation -> automationDao.update(automation)
        }
    }

    suspend fun updateAutomation(automation: IAutomation, scenes: List<Scene>) {
        when (automation) {
            is ClockAutomation -> updateClockAutomation(automation, scenes)
            is GeolocationAutomation -> updateGeoAutomation(automation, scenes)
            is ShakeAutomation -> updateShakeAutomation(automation, scenes)
        }
    }

    private suspend fun updateClockAutomation(automation: ClockAutomation, scenes: List<Scene>) {
        automationDao.update(automation)
        automationDao.deleteClockAutomationScenes(automation.automationId)
        addClockAutomationScenes(scenes, automation.automationId)
    }

    private suspend fun updateGeoAutomation(automation: GeolocationAutomation, scenes: List<Scene>) {
        automationDao.update(automation)
        automationDao.deleteGeoAutomationScenes(automation.automationId)
        addGeoAutomationScenes(scenes, automation.automationId)
    }

    private suspend fun updateShakeAutomation(automation: ShakeAutomation, scenes: List<Scene>) {
        automationDao.update(automation)
        automationDao.deleteShakeAutomationScenes(automation.automationId)
        addShakeAutomationScenes(scenes, automation.automationId)
    }

    suspend fun deleteAutomation(automation: IAutomation) {
        println(automation)
        when (automation) {
            is ClockAutomation -> automationDao.delete(automation)
            is GeolocationAutomation -> automationDao.delete(automation)
            is ShakeAutomation -> automationDao.delete(automation)
        }
    }
}