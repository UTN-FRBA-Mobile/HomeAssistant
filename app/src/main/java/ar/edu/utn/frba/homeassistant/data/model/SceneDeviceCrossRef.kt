package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["sceneId", "deviceId"])
data class SceneDeviceCrossRef(
    val sceneId: Long,
    val deviceId: Long
)