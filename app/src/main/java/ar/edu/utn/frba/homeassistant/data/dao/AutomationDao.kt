package ar.edu.utn.frba.homeassistant.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.Clock

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automation")
    fun getAll(): LiveData<List<Automation>>

    @Query("SELECT * FROM clock")
    fun getClockAutomations(): LiveData<List<Clock>>

//
//    @Transaction
//    @Query("SELECT * FROM scene")
//    fun getAllWithDevices(): LiveData<List<SceneWithDevices>>
//
//    @Insert
//    suspend fun insert(scene: Scene): Long
//
//    @Insert
//    suspend fun insertSceneDeviceCrossRef(sceneDeviceCrossRef: SceneDeviceCrossRef)
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
