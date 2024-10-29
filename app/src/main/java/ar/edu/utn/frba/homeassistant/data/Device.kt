package ar.edu.utn.frba.homeassistant.data

data class Device(
    val id: String,
    val name: String,
    val type: String,
    var isOn: Boolean = false  // Ajoutez cette propriété
)