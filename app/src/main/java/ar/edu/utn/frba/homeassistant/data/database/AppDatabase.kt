package ar.edu.utn.frba.homeassistant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationSceneCrossRef

@Database(entities = [
    Device::class,
    Scene::class,
    SceneDeviceCrossRef::class,
    ClockAutomation::class,
    // This should be an unique table, with type as composite pk with automationId and sceneid
    // IDK How to do it with room
    ClockAutomationSceneCrossRef::class,
    ShakeAutomationSceneCrossRef::class,
    GeolocationAutomationSceneCrossRef::class,
    GeolocationAutomation::class,
    ShakeAutomation::class
 ], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun sceneDao(): SceneDao
    abstract fun automationDao(): AutomationDao
}