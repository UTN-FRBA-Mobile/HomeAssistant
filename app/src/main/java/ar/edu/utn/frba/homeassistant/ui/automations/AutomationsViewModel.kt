package ar.edu.utn.frba.homeassistant.ui.automations

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.service.DEVICE_IDS
import ar.edu.utn.frba.homeassistant.service.REGISTER_SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.service.UNREGISTER_SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.utils.cancelAlarm
import ar.edu.utn.frba.homeassistant.utils.registerGeofenceReceiver
import ar.edu.utn.frba.homeassistant.utils.setAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "$GLOBAL_TAG#AUTOMATIONS_VIEW_MODEL"

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val application: Application
) : ViewModel() {
    val clockAutomations = repository.getClockAutomationsWithScenes()
    val geolocationAutomations = repository.getGeolocationAutomationsWithScenes()
    val shakeAutomations = repository.getShakeAutomationsWithScenes()
    val scenes = repository.getScenes()

    fun deleteAutomation(automation: IAutomation) {
        viewModelScope.launch {
            repository.deleteAutomation(automation)
            when (automation) {
                is ShakeAutomation -> unregisterShakeAutomation()
                // TODO: Add other automations unregister
            }
        }
    }

    fun toggleAutomation(automationWithScenes: IAutomationWithScenes, enable: Boolean) {
        viewModelScope.launch {
            val automation = automationWithScenes.automation
            val automationId = automation.automationId!!
            val scenes = automationWithScenes.scenes
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })

            when (automation) {
                is ClockAutomation -> {
                    if (enable) {
                        registerClockAutomation(devicesIds, automationId, automation)
                    } else {
                        unregisterClockAutomation(devicesIds, automationId, automation)
                    }
                    repository.updateAutomation(automation.copy(enabled = enable))
                }

                is GeolocationAutomation -> {
                    TODO("Not yet implemented")
                }

                is ShakeAutomation -> {
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

    fun addAutomation(automation: IAutomation, scenes: Set<Scene>) {
        val sceneNames = scenes.map { it.name }.joinToString(",") { it }
        val automationName = automation::class::simpleName
        Log.d(TAG, "[addAutomation]: Request to add $automationName. for scenes:  $sceneNames")

        viewModelScope.launch {
            val devicesIds = repository.getScenesDevicesIds(scenes.toList().map { it.sceneId })
            when (automation) {
                is ClockAutomation -> addClockAutomation(automation, scenes, devicesIds)
                is GeolocationAutomation -> addGeolocationAutomation(automation, scenes, devicesIds)
                is ShakeAutomation -> addShakeAutomation(automation, scenes, devicesIds)
                else -> Log.wtf(TAG, "[addAutomation]: Unknown automation type")
            }
        }
    }

    private suspend fun addClockAutomation(
        automation: ClockAutomation,
        scenes: Set<Scene>,
        devicesIds: List<Long>
    ) {
        Log.d(TAG, "[addClockAutomation]: Request to save automation in database")
        val id = repository.addAutomation(automation, scenes.toList())
        Log.d(TAG, "[addClockAutomation]: Automation successfully saved with id $id")
        Log.d(TAG, "[addAutomation#clockAutomation]: Setting alarm for automation")
        registerClockAutomation(devicesIds, id, automation)
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


    private suspend fun addGeolocationAutomation(
        automation: GeolocationAutomation,
        scenes: Set<Scene>,
        devicesIds: List<Long>
    ) {
        Log.d(TAG, "[addGeolocationAutomation]: Request to save automation in database")
        val id = repository.addAutomation(automation, scenes.toList())
        Log.d(TAG, "[addGeolocationAutomation]: Automation successfully saved with id $id")
        registerGeolocationAutomation(devicesIds, automation)

    }

    private fun registerGeolocationAutomation(
        devicesIds: List<Long>,
        automation: GeolocationAutomation
    ) {
        registerGeofenceReceiver(
            application.applicationContext,
            devicesIds.toLongArray(),
            automation
        )
    }

    private suspend fun addShakeAutomation(
        automation: ShakeAutomation,
        scenes: Set<Scene>,
        devicesIds: List<Long>
    ) {
        Log.d(TAG, "[addShakeAutomation]: Request to save automation in database")
        val id = repository.addAutomation(automation, scenes.toList())
        Log.d(TAG, "[addShakeAutomation]: Automation successfully saved with id $id")
        registerShakeAutomation(devicesIds)
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
