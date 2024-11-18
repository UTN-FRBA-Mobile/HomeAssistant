package ar.edu.utn.frba.homeassistant.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneDeviceCrossRef
import ar.edu.utn.frba.homeassistant.data.model.SceneWithDevices

@Dao
interface SceneDao {
    @Query("SELECT * FROM scene")
    fun getAll(): LiveData<List<Scene>>

    @Transaction
    @Query("SELECT * FROM scene")
    fun getAllWithDevices(): LiveData<List<SceneWithDevices>>

    @Insert
    suspend fun insert(scene: Scene): Long

    @Insert
    suspend fun insertSceneDeviceCrossRef(sceneDeviceCrossRef: SceneDeviceCrossRef)

    @Update
    suspend fun update(scene: Scene)

    @Delete
    suspend fun delete(scene: Scene)

    @Query("DELETE FROM sceneDeviceCrossRef WHERE sceneId = :sceneId")
    suspend fun deleteSceneDevices(sceneId: Long)
}