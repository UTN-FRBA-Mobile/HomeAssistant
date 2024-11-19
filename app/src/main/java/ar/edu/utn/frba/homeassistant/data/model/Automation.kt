package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class DAY_OF_WEEK {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

interface IAutomation {
    val automationId: Long?
    val name: String
    val isOn: Boolean
    val enabled: Boolean
    val type: String // It should not be needed, i should use polymorphism
}

//@Entity
//data class Automation (
//    @PrimaryKey(autoGenerate = true)
//    val automationId: Long,
//    val name: String,
//    var isOn: Boolean,
//    var enabled: Boolean,
//)

//data class AutomationWithScenes(
//    @Relation(
//        parentColumn = "automationId",
//        entityColumn = "sceneId",
//        associateBy = Junction(AutomationSceneCrossRef::class)
//    )
//    val scenes: List<Scene>,
//    @Embedded val automation: Automation
//)
//
//@Entity(primaryKeys = ["sceneId", "automationId"])
//data class AutomationSceneCrossRef(
//    val automationId: Long,
//    val sceneId: Long
//)

@Entity
data class ClockAutomation (
    @PrimaryKey(autoGenerate = true)
    override val automationId: Long?,
    val timeTurnOn: String,
    val timeTurnOff: String,
    override val isOn: Boolean = false,
    override val name: String,
    override val enabled: Boolean,
    //override val scenes: List<Scene>,
    override val type: String = "CLOCK"
): IAutomation

@Entity(primaryKeys = ["automationId", "day"])
data class ClockDaysCrossRef(
    val automationId: Long,
    val day: DAY_OF_WEEK
)

interface IAutomationWithScenes {
    val scenes : List<Scene>
    val automation: IAutomation
}

data class ClockAutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(AutomationSceneCrossRef::class)
    )
    override val scenes: List<Scene>,
    @Embedded override val automation: ClockAutomation
): IAutomationWithScenes

@Entity(primaryKeys = ["sceneId", "automationId"])
data class AutomationSceneCrossRef(
    val automationId: Long,
    val sceneId: Long
)

@Entity
data class GeolocationAutomation (
    @PrimaryKey(autoGenerate = true)
    override val automationId: Long?,
    val latitude: Double,
    val longitude: Double,
    override val isOn: Boolean = false,
    override val name: String,
    override val enabled: Boolean = true,
    //override val scenes: List<Scene>,
    override val type: String = "GEOLOCATION"
): IAutomation

data class GeolocationAutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(AutomationSceneCrossRef::class)
    )
    override val scenes: List<Scene>,
    @Embedded override val automation: GeolocationAutomation
): IAutomationWithScenes

//@Entity
//data class GeolocationAutomation (
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
//    val automation: Automation,
//    val latitude: Double,
//    val longitude: Double,
//    val radius: Double,
//)
//
//@Entity
//data class ShakeAutomation (
//    @PrimaryKey
//    val id: Long,
//    val automation: Automation,
//)