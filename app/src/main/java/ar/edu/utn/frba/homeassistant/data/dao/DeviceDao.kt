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

    @Query("SELECT * FROM device WHERE deviceId = :id")
    suspend fun getById(id: Long): Device?

    @Query("SELECT * FROM device WHERE deviceId IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<Device>

    @Insert
    suspend fun insert(device: Device)

    @Update
    suspend fun update(device: Device)

    @Query("Update device SET isOn = :isOn WHERE deviceId = :id")
    suspend fun updateIsOn(id: Long, isOn: Boolean)

    @Delete
    suspend fun delete(device: Device)
}