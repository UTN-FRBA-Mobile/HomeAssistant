package ar.edu.utn.frba.homeassistant.data.repository

import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.AutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.Device
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

    fun getAutomationsWithScenes() = automationDao.getAllWithScenes()

    suspend fun addAutomation(automation: Automation, scenes: List<Scene>): Long {
        val id = automationDao.insert(automation)
        addAutomationScenes(scenes, id)
        return id
    }

    private suspend fun addAutomationScenes(scenes: List<Scene>, automationId: Long) {
        scenes.forEach {
            automationDao.insertAutomationSceneCrossRef(
                AutomationSceneCrossRef(
                    it.sceneId,
                    automationId
                )
            )
        }
    }

    suspend fun updateAutomation(automation: Automation) {
        automationDao.update(automation)
    }

    suspend fun updateAutomation(automation: Automation, scenes: List<Scene>) {
        automationDao.deleteAutomationScenes(automation.automationId)
        addAutomationScenes(scenes, automation.automationId)
        automationDao.update(automation)
    }

    suspend fun deleteAutomation(automation: Automation) {
        automationDao.delete(automation)
    }
}