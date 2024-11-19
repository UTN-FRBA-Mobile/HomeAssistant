package ar.edu.utn.frba.homeassistant.ui.automations

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.network.UdpService
import ar.edu.utn.frba.homeassistant.ui.SnackbarManager
import ar.edu.utn.frba.homeassistant.utils.Receivers.AlarmReceiver
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val udpService: UdpService,
    private val application: Application
) : ViewModel() {

    val clockAutomations = repository.getClockAutomationsWithScenes();
    val geolocationAutomations = repository.getGeolocationAutomationsWithScenes();
    val shakeAutomations = repository.getShakeAutomationsWithScenes();
    val scenes = repository.getScenes();
//
//    fun addDevice(id: Long, name: String, type: String) {
//        viewModelScope.launch {
//            val existingDevice = repository.getDeviceById(id)
//            if (existingDevice != null) {
//                SnackbarManager.showMessage("Device with ID $id already exists.")
//            } else {
//                repository.addDevice(id, name, type)
//            }
//        }
//    }
//
//    fun deleteDevice(device: Device) {
//        viewModelScope.launch {
//            repository.deleteDevice(device)
//        }
//    }
//
//    fun toggleDevice(device: Device, isOn: Boolean) {
//        viewModelScope.launch(Dispatchers.IO) {
//            udpService.sendUdpMessage(device.deviceId, if (isOn) "toggle:on" else "toggle:off")
//            repository.updateDevice(device.copy(isOn = isOn))
//        }
//    }

    fun deleteAutomation(automation: Any) {
        viewModelScope.launch {
            SnackbarManager.showMessage("Automation deleted.")
        }
    }

    fun toggleAutomation(automation: IAutomationWithScenes, b: Boolean) {
        viewModelScope.launch {
            SnackbarManager.showMessage("Automation toggled.")
        }
    }

    fun addAutomation(scenes: Set<Scene>): (IAutomation) -> Unit {
        return fun(automation: IAutomation) {
            when(automation.type){
                "CLOCK" -> {
                    val clockAutomation = automation as ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
                    val clockAutomationWithScenes = ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes(scenes.toList(), clockAutomation)
                    viewModelScope.launch {
                        val id = repository.addAutomation(clockAutomationWithScenes)
                        SnackbarManager.showMessage("${automation.name} added for scenes ${scenes.map { it.name }.joinToString { it }}.")

                        val context = application.applicationContext
                        val intent = Intent(context, AlarmReceiver::class.java)
                        intent.putExtra("automationId", id)

                        val alarmManager = context.getSystemService(AlarmManager::class.java)
                        val time = clockAutomation.time
                        val hour = time.split(":")[0].toInt()
                        val minute = time.split(":")[1].toInt()

                        val days = mapOf(
                            "Monday" to clockAutomation.monday,
                            "Tuesday" to clockAutomation.tuesday,
                            "Wednesday" to clockAutomation.wednesday,
                            "Thursday" to clockAutomation.thursday,
                            "Friday" to clockAutomation.friday,
                            "Saturday" to clockAutomation.saturday,
                            "Sunday" to clockAutomation.sunday
                        )

                        for ((day, shouldExecute) in days) {
                            if (shouldExecute) {
                                val dayOfWeek = when (day) {
                                    "Monday" -> java.util.Calendar.MONDAY
                                    "Tuesday" -> java.util.Calendar.TUESDAY
                                    "Wednesday" -> java.util.Calendar.WEDNESDAY
                                    "Thursday" -> java.util.Calendar.THURSDAY
                                    "Friday" -> java.util.Calendar.FRIDAY
                                    "Saturday" -> java.util.Calendar.SATURDAY
                                    "Sunday" -> java.util.Calendar.SUNDAY
                                    else -> -1
                                }
                                val calendar = java.util.Calendar.getInstance()
                                calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
                                calendar.set(java.util.Calendar.MINUTE, minute)
                                calendar.set(java.util.Calendar.SECOND, 0)
                                if (dayOfWeek != -1) {
                                    calendar.set(java.util.Calendar.DAY_OF_WEEK, dayOfWeek)
                                    println(calendar.time)
                                    println(calendar.timeInMillis)
                                    println(calendar.timeZone)
                                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                                        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                                        println("TEST")
                                    }
                                    alarmManager.setRepeating(
                                        AlarmManager.RTC_WAKEUP,
                                        calendar.timeInMillis,
                                        1000 * 60 * 60 * 24 * 7,
                                        PendingIntent.getBroadcast(context, "0$dayOfWeek$id".toInt(), intent, PendingIntent.FLAG_IMMUTABLE)
                                    )
                                }
                            }
                        }
                    }
                }
                "GEOLOCATION" -> {
                    val geolocationAutomation = automation as ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
                    val geolocationAutomationWithScenes = ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes(scenes.toList(), geolocationAutomation)
                    viewModelScope.launch {
                        repository.addAutomation(geolocationAutomationWithScenes)
                        SnackbarManager.showMessage("${automation.name} added for scenes ${scenes.map { it.name }.joinToString { it }}.")
                    }
                }
                "SHAKE" -> {
                    val shakeAutomation = automation as ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
                    val shakeAutomationWithScenes = ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationWithScenes(scenes.toList(), shakeAutomation)
                    viewModelScope.launch {
                        repository.addAutomation(shakeAutomationWithScenes)
                        SnackbarManager.showMessage("${automation.name} added for scenes ${scenes.map { it.name }.joinToString { it }}.")
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
