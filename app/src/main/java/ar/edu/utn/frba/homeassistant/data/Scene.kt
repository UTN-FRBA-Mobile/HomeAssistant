package ar.edu.utn.frba.homeassistant.data

import ar.edu.utn.frba.homeassistant.data.model.Device

data class Scene(
    val id: String,
    val name: String,
    val devices: List<Device>
)