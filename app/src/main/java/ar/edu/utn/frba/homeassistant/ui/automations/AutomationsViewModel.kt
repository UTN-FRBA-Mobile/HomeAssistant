package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.Device
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

    val automations = repository.getAutomations();
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

    fun toggleAutomation(automation: Automation, b: Boolean) {
        viewModelScope.launch {
            SnackbarManager.showMessage("Automation toggled.")
        }
    }

    fun addAutomation(l: Long, s: String, s1: String) {
        viewModelScope.launch {
            SnackbarManager.showMessage("Automation added.")
        }

    }
}
