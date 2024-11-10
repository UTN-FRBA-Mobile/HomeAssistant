package ar.edu.utn.frba.homeassistant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ar.edu.utn.frba.homeassistant.data.dao.DeviceDao
import ar.edu.utn.frba.homeassistant.data.model.Device

@Database(entities = [Device::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}