package ar.edu.utn.frba.homeassistant.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ar.edu.utn.frba.homeassistant.data.model.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device")
    fun getAll(): LiveData<List<Device>>

    @Insert
    suspend fun insert(device: Device)

    @Update
    suspend fun update(device: Device)

    @Delete
    suspend fun delete(device: Device)
}