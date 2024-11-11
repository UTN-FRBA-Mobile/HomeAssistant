package ar.edu.utn.frba.homeassistant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.dao.SceneDao
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef

@Database(entities = [Device::class, Scene::class, SceneDeviceCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun sceneDao(): SceneDao
}