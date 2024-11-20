package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SceneWithDevices(
    @Embedded val scene: Scene,
    @Relation(
        parentColumn = "sceneId",
        entityColumn = "deviceId",
        associateBy = Junction(SceneDeviceCrossRef::class)
    )
    val devices: List<Device>,
    var isOn: Boolean = false
)
