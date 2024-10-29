package ar.edu.utn.frba.homeassistant.data

data class Scene(
    val id: String,
    val name: String,
    val devices: List<Device>
)