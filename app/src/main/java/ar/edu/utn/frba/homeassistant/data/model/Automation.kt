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

@Entity
data class Automation (
    @PrimaryKey(autoGenerate = true)
    val automationId: Long,
    val name: String,
    var isOn: Boolean,
    var enabled: Boolean,
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

@Entity(primaryKeys = ["sceneId", "automationId"])
data class AutomationSceneCrossRef(
    val automationId: Long,
    val sceneId: Long
)

@Entity
data class Clock (
    @PrimaryKey(autoGenerate = true)
    val clockAutomationId: Long,
    val timeTurnOn: String,
    val timeTurnOff: String,
//    val days: List<DaysOfWeek>,
)

@Entity(primaryKeys = ["clockAutomationId", "day"])
data class ClockDaysCrossRef(
    val clockAutomationId: Long,
    val day: DAY_OF_WEEK
)


data class ClockAutomation(
    @Relation(
        parentColumn = "clockAutomationId",
        entityColumn = "automationId",
    )
    val automation: Automation,

    @Relation(
        parentColumn = "clockAutomationId",
        entityColumn = "day",
        associateBy = Junction(ClockDaysCrossRef::class)
    )
    val days: List<DAY_OF_WEEK>,

    @Embedded val clock: Clock
)


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