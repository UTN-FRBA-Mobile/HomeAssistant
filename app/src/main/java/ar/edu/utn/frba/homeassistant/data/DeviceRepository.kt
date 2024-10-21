package ar.edu.utn.frba.homeassistant.data

class DeviceRepository {
    private val devices = listOf(
        Device("1", "Living Room Light", "Light"),
        Device("2", "Bedroom AC", "Air Conditioner"),
        Device("3", "Kitchen Thermostat", "Thermostat")
    )

    fun getDevices(): List<Device> {
        return devices
    }

    fun getDeviceById(deviceId: String): Device? {
        return devices.find { it.id == deviceId }
    }
}
