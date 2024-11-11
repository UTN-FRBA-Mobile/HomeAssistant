package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Scene(
    @PrimaryKey(autoGenerate = true) val sceneId: Long = 0,
    val name: String,
)