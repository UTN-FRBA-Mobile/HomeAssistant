package ar.edu.utn.frba.homeassistant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ar.edu.utn.frba.homeassistant.data.dao.AutomationDao
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.AutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockDaysCrossRef
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef

@Database(entities = [
    Device::class,
    Scene::class,
    SceneDeviceCrossRef::class,
    ClockAutomation::class,
    AutomationSceneCrossRef::class,
    ClockDaysCrossRef::class
 ], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun sceneDao(): SceneDao
    abstract fun automationDao(): AutomationDao
}