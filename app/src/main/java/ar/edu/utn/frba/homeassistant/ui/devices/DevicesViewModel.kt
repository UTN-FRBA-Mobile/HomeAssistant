package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.homeassistant.data.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DevicesViewModel : ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices

    init {
        _devices.value = listOf(
            Device("1", "Living Room Light", "Light"),
            Device("2", "Bedroom AC", "Air Conditioner"),
            Device("3", "Kitchen Thermostat", "Thermostat")
        )
    }

    fun addDevice(name: String, type: String) {
        val newDevice = Device(id = (_devices.value.size + 1).toString(), name = name, type = type)
        _devices.update { it + newDevice }
    }

    fun deleteDevice(device: Device) {
        _devices.update { devices -> devices.filter { it.id != device.id } }
    }

}
