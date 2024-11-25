package ar.edu.utn.frba.homeassistant.ui.automations

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.AutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.CLOCK_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.GEOLOCATION_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.toClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.toGeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.service.DEVICE_IDS
import ar.edu.utn.frba.homeassistant.service.REGISTER_SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.service.UNREGISTER_SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.utils.cancelAlarm
import ar.edu.utn.frba.homeassistant.utils.registerGeofenceReceiver
import ar.edu.utn.frba.homeassistant.utils.setAlarm
import ar.edu.utn.frba.homeassistant.utils.unregisterGeofenceReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "$GLOBAL_TAG#AUTOMATIONS_VIEW_MODEL"

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val application: Application
) : ViewModel() {
    val automationsWithScenes = repository.getAutomationsWithScenes()
    val scenes = repository.getScenes()

    fun deleteAutomation(automationWithScenes: AutomationWithScenes) {
        viewModelScope.launch {
            val automation = automationWithScenes.automation
            val automationId = automation.automationId
            val scenes = automationWithScenes.scenes
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })

            repository.deleteAutomation(automation)
            when (automation.type) {
                CLOCK_AUTOMATION -> unregisterClockAutomation(
                    devicesIds,
                    automationId,
                    automation.toClockAutomation()
                )

                SHAKE_AUTOMATION -> unregisterShakeAutomation()
                GEOLOCATION_AUTOMATION -> unregisterGeolocationAutomation(
                    devicesIds,
                    automation.toGeolocationAutomation()
                )
            }
        }
    }

    fun toggleAutomation(automationWithScenes: AutomationWithScenes, enable: Boolean) {
        viewModelScope.launch {
            val automation = automationWithScenes.automation
            val automationId = automation.automationId
            val scenes = automationWithScenes.scenes
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })

            when (automation.type) {
                CLOCK_AUTOMATION -> {
                    if (enable) {
                        registerClockAutomation(devicesIds, automationId, automation.toClockAutomation())
                    } else {
                        unregisterClockAutomation(devicesIds, automationId, automation.toClockAutomation())
                    }
                    repository.updateAutomation(automation.copy(enabled = enable))
                }

                GEOLOCATION_AUTOMATION -> {
                    if (enable) {
                        registerGeolocationAutomation(devicesIds, automation.toGeolocationAutomation(), automationId)
                    } else {
                        unregisterGeolocationAutomation(devicesIds, automation.toGeolocationAutomation())
                    }
                    repository.updateAutomation(automation.copy(enabled = enable))
                }

                SHAKE_AUTOMATION -> {
                    if (enable) {
                        registerShakeAutomation(devicesIds)
                    } else {
                        unregisterShakeAutomation()
                    }
                    repository.updateAutomation(automation.copy(enabled = enable))
                }

                else -> {
                    Log.wtf(TAG, "[toggleAutomation]: Unknown automation type")
                }

            }

        }
    }

    fun addAutomation(automation: Automation, scenes: Set<Scene>) {
        val sceneNames = scenes.map { it.name }.joinToString(", ") { it }
        val automationName = automation::class::simpleName.get()
        Log.d(TAG, "[addAutomation]: Request to add $automationName for scenes:  $sceneNames")

        viewModelScope.launch {
            Log.d(TAG, "[addClockAutomation]: Request to save automation in database")
            val id = repository.addAutomation(automation, scenes.toList())
            Log.d(TAG, "[addClockAutomation]: Automation successfully saved with id $id")
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })
            when (automation.type) {
                CLOCK_AUTOMATION -> registerClockAutomation(devicesIds, id, automation.toClockAutomation())
                GEOLOCATION_AUTOMATION -> registerGeolocationAutomation(devicesIds, automation.toGeolocationAutomation(), id)
                SHAKE_AUTOMATION -> registerShakeAutomation(devicesIds)
                else -> Log.wtf(TAG, "[addAutomation]: Unknown automation type")
            }
        }
    }

    fun updateAutomation(
        originalAutomation: Automation,
        originalScenes: Set<Scene>,
        automation: Automation,
        scenes: Set<Scene>
    ) {
        val sceneNames = scenes.map { it.name }.joinToString(", ") { it }
        val automationName = automation::class::simpleName.get()
        Log.d(TAG, "[addAutomation]: Request to update $automationName for scenes:  $sceneNames")

        viewModelScope.launch {
            val automationId = automation.automationId
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })
            val originalDevicesIds =
                repository.getScenesDevicesIds(originalScenes.toList().map { it.sceneId })
            repository.updateAutomation(automation, scenes.toList())
            when (automation.type) {
                CLOCK_AUTOMATION -> {
                    unregisterClockAutomation(
                        originalDevicesIds,
                        automationId,
                        originalAutomation.toClockAutomation()
                    )
                    registerClockAutomation(devicesIds, automationId, automation.toClockAutomation())
                }

                GEOLOCATION_AUTOMATION -> {
                    unregisterGeolocationAutomation(
                        originalDevicesIds,
                        originalAutomation.toGeolocationAutomation()
                    )
                    registerGeolocationAutomation(devicesIds, automation.toGeolocationAutomation(), automationId)
                }

                SHAKE_AUTOMATION -> {
                    unregisterShakeAutomation()
                    registerShakeAutomation(devicesIds)
                }

                else -> Log.wtf(TAG, "[addAutomation]: Unknown automation type")
            }
        }
    }

    private fun registerClockAutomation(
        devicesIds: List<Long>,
        id: Long,
        automation: ClockAutomation
    ) {
        setAlarm(
            application.applicationContext,
            id,
            automation,
            devicesIds.toLongArray()
        )
    }

    private fun unregisterClockAutomation(
        devicesIds: List<Long>,
        id: Long,
        automation: ClockAutomation
    ) {
        cancelAlarm(
            application.applicationContext,
            id,
            automation,
            devicesIds.toLongArray()
        )
    }

    private fun registerGeolocationAutomation(
        devicesIds: List<Long>,
        automation: GeolocationAutomation,
        automationId: Long
    ) {
        registerGeofenceReceiver(
            application.applicationContext,
            devicesIds.toLongArray(),
            automation,
            automationId
        )
    }

    private fun unregisterGeolocationAutomation(
        devicesIds: List<Long>,
        automation: GeolocationAutomation
    ) {
        unregisterGeofenceReceiver(
            application.applicationContext,
            devicesIds.toLongArray(),
            automation
        )
    }

    private fun registerShakeAutomation(devicesIds: List<Long>) {
        val intent = Intent(REGISTER_SHAKE_AUTOMATION)
        intent.putExtra(DEVICE_IDS, devicesIds.toLongArray())
        application.applicationContext.sendBroadcast(intent)
    }

    private fun unregisterShakeAutomation() {
        val intent = Intent(UNREGISTER_SHAKE_AUTOMATION)
        application.applicationContext.sendBroadcast(intent)
    }
}
