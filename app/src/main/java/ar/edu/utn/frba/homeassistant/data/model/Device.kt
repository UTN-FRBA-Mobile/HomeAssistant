package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    var isOn: Boolean = false
)