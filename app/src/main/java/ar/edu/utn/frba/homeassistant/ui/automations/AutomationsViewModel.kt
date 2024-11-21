package ar.edu.utn.frba.homeassistant.ui.automations

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.SENSOR_SERVICE
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ar.edu.utn.frba.homeassistant.network.UdpService
import ar.edu.utn.frba.homeassistant.ui.SnackbarManager
import ar.edu.utn.frba.homeassistant.utils.Receivers.GeofenceBroadcastReceiver
import ar.edu.utn.frba.homeassistant.utils.buildGeofence
import ar.edu.utn.frba.homeassistant.utils.buildGeofenceRequest
import ar.edu.utn.frba.homeassistant.utils.cancelAlarm
import ar.edu.utn.frba.homeassistant.utils.registerShakeSensor
import ar.edu.utn.frba.homeassistant.utils.setAlarm
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val udpService: UdpService,
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
            automation.automation.automationId?.let {
                if (enable) {
                    setAlarm(
                        application.applicationContext,
                        it,
                        automation.automation as ClockAutomation
                    )
                } else {
                    cancelAlarm(
                        application.applicationContext,
                        it,
                        automation.automation as ClockAutomation
                    )
                }

                repository.updateAutomation((automation.automation as ClockAutomation).copy(enabled = enable))
            }

        }
    }

    fun addAutomation(scenes: Set<Scene>): (IAutomation) -> Unit {
        return fun(automation: IAutomation) {
            when (automation) {
                is ClockAutomation -> {
                    val clockAutomationWithScenes =
                        ClockAutomationWithScenes(
                            scenes.toList(),
                            automation
                        )
                    viewModelScope.launch {
                        val id = repository.addAutomation(clockAutomationWithScenes)
                        SnackbarManager.showMessage(
                            "${automation.name} added for scenes ${
                                scenes.map { it.name }.joinToString { it }
                            }."
                        )

                        val context = application.applicationContext
                        setAlarm(context, id, automation)
                    }
                }

                is GeolocationAutomation -> {
                    val geolocationAutomationWithScenes =
                        GeolocationAutomationWithScenes(
                            scenes.toList(),
                            automation
                        )
                    viewModelScope.launch {
                        val id = repository.addAutomation(geolocationAutomationWithScenes)
                        SnackbarManager.showMessage(
                            "${automation.name} added for scenes ${
                                scenes.map { it.name }.joinToString { it }
                            }."
                        )
                        // https://medium.com/@KaushalVasava/geofence-in-android-8add1f6b9be1
                        val geofencingClient =
                            LocationServices.getGeofencingClient(application.applicationContext)
                        val latitude = automation.latitude
                        val longitude = automation.longitude
                        val radius = 100f // TODO: Unhardcode
                        val geofence =
                            buildGeofence("$latitude,$longitude", latitude, longitude, radius)
                        val geofenceRequest = buildGeofenceRequest(geofence)
                        val intent = Intent(
                            application.applicationContext,
                            GeofenceBroadcastReceiver::class.java
                        )
                        intent.putExtra("automationId", id)
                        val pendingIntent = PendingIntent.getBroadcast(
                            application.applicationContext,
                            0,
                            intent,
                            PendingIntent.FLAG_MUTABLE
                        )

                        // It must be there or linter will fail.
                        if (ActivityCompat.checkSelfPermission(
                                application.applicationContext,
                                ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            geofencingClient.addGeofences(geofenceRequest, pendingIntent).run {
                                addOnSuccessListener {
                                    Log.d("Geofence", "Geofence added")
                                }
                                addOnFailureListener { exception ->
                                    Log.d("Geofence", "Geofence not added: ${exception}")
                                }
                            }
                        } else {
                            // TODO: What should we do if we don't have permissions?
                        }
                    }
                }

                is ShakeAutomation -> {
                    val shakeAutomationWithScenes =
                        ShakeAutomationWithScenes(
                            scenes.toList(),
                            automation
                        )
                    viewModelScope.launch {
                        val id = repository.addAutomation(shakeAutomationWithScenes)
                        SnackbarManager.showMessage(
                            "${automation.name} added for scenes ${
                                scenes.map { it.name }.joinToString { it }
                            }."
                        )
                        val sensorManager: SensorManager =
                            application.applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
                        registerShakeSensor(sensorManager) {
                            println("Shake detected!! - Automation id: $id")
                            println("TODO: Trigger automation")
                        }
                    }
                }

                else -> {
                    viewModelScope.launch {
                        SnackbarManager.showMessage("Automation type not supported.")
                    }
                }
            }
        }
    }

}
