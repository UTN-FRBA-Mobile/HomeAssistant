package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Automation(
    @PrimaryKey(autoGenerate = true)
    val automationId: Long = 0,
    val name: String,
    val isOn: Boolean = false,
    val enabled: Boolean = true,
    val type: String,
    // ClockAutomation
    val time: String? = null,
    val shouldTurnOn: Boolean? = null,
    val monday: Boolean? = null,
    val tuesday: Boolean? = null,
    val wednesday: Boolean? = null,
    val thursday: Boolean? = null,
    val friday: Boolean? = null,
    val saturday: Boolean? = null,
    val sunday: Boolean? = null,
    // GeolocationAutomation
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Float? = null,
)

@Entity(primaryKeys = ["sceneId", "automationId"])
data class AutomationSceneCrossRef(
    val sceneId: Long,
    val automationId: Long
)

data class AutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(AutomationSceneCrossRef::class)
    )
    val scenes: List<Scene>,
    @Embedded val automation: Automation
)

data class ClockAutomation(
    val automationId: Long,
    val name: String,
    val isOn: Boolean,
    val enabled: Boolean,
    val time: String,
    val shouldTurnOn: Boolean,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
)


const val CLOCK_AUTOMATION = "ClockAutomation"

fun Automation.toClockAutomation(): ClockAutomation {
    if (type != CLOCK_AUTOMATION) throw Exception("Tried to convert $type to ClockAutomation")
    return ClockAutomation(
        automationId = automationId,
        name = name,
        isOn = isOn,
        enabled = enabled,
        time = time ?: "",
        shouldTurnOn = shouldTurnOn ?: false,
        monday = monday ?: false,
        tuesday = tuesday ?: false,
        wednesday = wednesday ?: false,
        thursday = thursday ?: false,
        friday = friday ?: false,
        saturday = saturday ?: false,
        sunday = sunday ?: false
    )
}

data class GeolocationAutomation(
    val automationId: Long,
    val name: String,
    val isOn: Boolean,
    val enabled: Boolean,
    val latitude: Double,
    val longitude: Double,
    val radius: Float
)


const val GEOLOCATION_AUTOMATION = "GeolocationAutomation"

fun Automation.toGeolocationAutomation(): GeolocationAutomation {
    if (type != GEOLOCATION_AUTOMATION) throw Exception("Tried to convert $type to GeolocationAutomation")
    return GeolocationAutomation(
        automationId = automationId,
        name = name,
        isOn = isOn,
        enabled = enabled,
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        radius = radius ?: 0f
    )
}

data class ShakeAutomation(
    val automationId: Long,
    val name: String,
    val isOn: Boolean,
    val enabled: Boolean,
)


const val SHAKE_AUTOMATION = "ShakeAutomation"

fun Automation.toShakeAutomation(): ShakeAutomation {
    if (type != SHAKE_AUTOMATION) throw Exception("Tried to convert $type to ShakeAutomation")
    return ShakeAutomation(
        automationId = automationId,
        name = name,
        isOn = isOn,
        enabled = enabled,
    )
}
