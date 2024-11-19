package ar.edu.utn.frba.homeassistant.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.SceneWithDevices
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomationWithScenes

@Dao
interface AutomationDao {
    @Query("SELECT * FROM clockautomation")
    fun getAll(): LiveData<List<ClockAutomation>>

    @Transaction
    @Query("SELECT * FROM clockautomation")
    fun getAllClockWithScenes(): LiveData<List<ClockAutomationWithScenes>>

    @Transaction
    @Query("SELECT * FROM geolocationautomation")
    fun getAllGeolocationWithScenes(): LiveData<List<GeolocationAutomationWithScenes>>

    @Query("SELECT * FROM shakeautomation")
    fun getAllShakeWithScenes(): LiveData<List<ShakeAutomationWithScenes>>

//
//    @Transaction
//    @Query("SELECT * FROM scene")
//    fun getAllWithDevices(): LiveData<List<SceneWithDevices>>
//
    @Insert
    suspend fun insert(automation: ClockAutomation): Long

    @Insert
    suspend fun insert(automation: GeolocationAutomation): Long

    @Insert
    suspend fun insert(automation: ShakeAutomation): Long

    @Insert
    suspend fun insertAutomationSceneCrossRef(automationSceneCrossRef: ClockAutomationSceneCrossRef)

    @Insert
    suspend fun insertAutomationSceneCrossRef(automationSceneCrossRef: ShakeAutomationSceneCrossRef)

    @Insert
    suspend fun insertAutomationSceneCrossRef(automationSceneCrossRef: GeolocationAutomationSceneCrossRef)

    @Update
    suspend fun update(automation: ClockAutomation)

    @Update
    suspend fun update(automation: GeolocationAutomation)

    @Update
    suspend fun update(automation: ShakeAutomation)
//
//    @Update
//    suspend fun update(scene: Scene)
//
//    @Delete
//    suspend fun delete(scene: Scene)
//
//    @Query("DELETE FROM sceneDeviceCrossRef WHERE sceneId = :sceneId")
//    suspend fun deleteSceneDevices(sceneId: Long)
}
