package ar.edu.utn.frba.homeassistant.ui.automations

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.network.DEVICE_IDS
import ar.edu.utn.frba.homeassistant.network.REGISTER_SHAKE_AUTOMATION
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
        }
    }

    fun toggleAutomation(automation: IAutomationWithScenes, enable: Boolean) {
        viewModelScope.launch {
            val scenesWithDevices =
                repository.getSceneByIdWithDevices(automation.scenes.toList().map { it.sceneId })
            val devicesIds =
                scenesWithDevices.flatMap { it.devices }.map { it.deviceId }

            when(automation){
                is ClockAutomationWithScenes -> {
                    if (enable) {
                        setAlarm(
                            application.applicationContext,
                            automation.automation.automationId!!,
                            automation.automation,
                            devicesIds.toLongArray()
                        )
                    } else {
                        cancelAlarm(
                            application.applicationContext,
                            automation.automation.automationId!!,
                            automation.automation,
                            devicesIds.toLongArray()
                        )
                    }
                    repository.updateAutomation((automation.automation).copy(enabled = enable))
                }
                is GeolocationAutomationWithScenes -> {
                    TODO("Not yet implemented")
                }
                is ShakeAutomationWithScenes -> {
                    TODO("Not yet implemented")
                }
                else -> {
                    Log.wtf(TAG, "[toggleAutomation]: Unknown automation type")
                }

            }

        }
    }

    fun addAutomation(automation: IAutomation, scenes: Set<Scene>) {
        Log.d(
            TAG,
            "[addAutomation]: Request to add automations for scenes:  ${
                scenes.map { it.name }.joinToString(",") { it }
            }"
        )
        when (automation) {
            is ClockAutomation -> {
                Log.d(TAG, "[addAutomation#clockAutomation]: Request to add clock automation")
                viewModelScope.launch {
                    Log.d(
                        TAG,
                        "[addAutomation#clockAutomation#viewModelScope]: Request to save automation in database"
                    )
                    val id = repository.addAutomation(automation, scenes.toList())
                    Log.d(
                        TAG,
                        "[addAutomation#clockAutomation]: Automation successfully saved with id $id"
                    )
                    Log.d(TAG, "[addAutomation#clockAutomation]: Setting alarm for automation")
                    val scenesWithDevices =
                        repository.getSceneByIdWithDevices(scenes.toList().map { it.sceneId })
                    val devicesIds =
                        scenesWithDevices.flatMap { it.devices }.map { it.deviceId }
                    setAlarm(
                        application.applicationContext,
                        id,
                        automation,
                        devicesIds.toLongArray()
                    )
                }
            }

            is GeolocationAutomation -> {
                Log.d(
                    TAG,
                    "[addAutomation#geolocationAutomation]: Request to add geolocation automation"
                )
                viewModelScope.launch {
                    Log.d(
                        TAG,
                        "[addAutomation#geolocationAutomation#viewModelScope]: Request to save automation in database"
                    )
                    val id = repository.addAutomation(automation, scenes.toList())
                    Log.d(
                        TAG,
                        "[addAutomation#geolocationAutomation]: Automation successfully saved with id $id"
                    )
                    val scenesWithDevices =
                        repository.getSceneByIdWithDevices(scenes.toList().map { it.sceneId })
                    val devicesIds =
                        scenesWithDevices.flatMap { it.devices }.map { it.deviceId }

                    registerGeofenceReceiver(
                        application.applicationContext,
                        devicesIds.toLongArray(),
                        automation
                    )

                }
            }

            is ShakeAutomation -> {
                viewModelScope.launch {
                    Log.d(
                        TAG,
                        "[addAutomation#shakeAutomation]: Request to save automation in database"
                    )
                    val id = repository.addAutomation(automation, scenes.toList())
                    Log.d(
                        TAG,
                        "[addAutomation#shakeAutomation]: Automation successfully saved with id $id"
                    )
                    val scenesWithDevices =
                        repository.getSceneByIdWithDevices(scenes.toList().map { it.sceneId })
                    val devicesIds =
                        scenesWithDevices.flatMap { it.devices }.map { it.deviceId }
                    Log.d(TAG, "[addAutomation#shakeAutomation]: Sending broadcast")
                    val intent = Intent(REGISTER_SHAKE_AUTOMATION)
                    intent.putExtra(DEVICE_IDS, devicesIds.toLongArray())
                    application.applicationContext.sendBroadcast(intent)
                    Log.d(TAG, "[addAutomation#shakeAutomation]: Broadcast sent")
                }
            }

            else -> {
                viewModelScope.launch {
                    Log.wtf(TAG, "[addAutomation]: Unknown automation type")
                }
            }
        }
    }

}
