package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.network.UdpService
import ar.edu.utn.frba.homeassistant.ui.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val udpService: UdpService
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
                        repository.addAutomation(clockAutomationWithScenes)
                        SnackbarManager.showMessage("${automation.name} added for scenes ${scenes.map { it.name }.joinToString { it }}.")
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
