package ar.edu.utn.frba.homeassistant.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

interface IAutomation {
    val automationId: Long
    val name: String
    val isOn: Boolean
    val enabled: Boolean
}

@Entity
data class ClockAutomation(
    @PrimaryKey(autoGenerate = true)
    override val automationId: Long = 0,
    val time: String,
    override val isOn: Boolean = false,
    override val name: String,
    override val enabled: Boolean,
    val shouldTurnOn: Boolean,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
) : IAutomation

interface IAutomationWithScenes {
    val scenes: List<Scene>
    val automation: IAutomation
}

data class ClockAutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(ClockAutomationSceneCrossRef::class)
    )
    override val scenes: List<Scene>,
    @Embedded override val automation: ClockAutomation
) : IAutomationWithScenes


interface IAutomationSceneCrossRef {
    val automationId: Long
    val sceneId: Long
}

@Entity(primaryKeys = ["sceneId", "automationId"])
data class ClockAutomationSceneCrossRef(
    override val sceneId: Long,
    override val automationId: Long
) : IAutomationSceneCrossRef

@Entity(primaryKeys = ["sceneId", "automationId"])
data class GeolocationAutomationSceneCrossRef(
    override val sceneId: Long,
    override val automationId: Long
) : IAutomationSceneCrossRef

@Entity
data class GeolocationAutomation(
    @PrimaryKey(autoGenerate = true)
    override val automationId: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    override val isOn: Boolean = false,
    override val name: String,
    override val enabled: Boolean = true,
) : IAutomation

data class GeolocationAutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(GeolocationAutomationSceneCrossRef::class)
    )
    override val scenes: List<Scene>,
    @Embedded override val automation: GeolocationAutomation
) : IAutomationWithScenes


@Entity
data class ShakeAutomation(
    @PrimaryKey(autoGenerate = true)
    override val automationId: Long = 0,
    override val isOn: Boolean = false,
    override val name: String,
    override val enabled: Boolean = true,
    val threshold: Int = 100
) : IAutomation

data class ShakeAutomationWithScenes(
    @Relation(
        parentColumn = "automationId",
        entityColumn = "sceneId",
        associateBy = Junction(ShakeAutomationSceneCrossRef::class)
    )
    override val scenes: List<Scene>,
    @Embedded override val automation: ShakeAutomation
) : IAutomationWithScenes

@Entity(primaryKeys = ["sceneId", "automationId"])
data class ShakeAutomationSceneCrossRef(
    override val sceneId: Long,
    override val automationId: Long
) : IAutomationSceneCrossRef