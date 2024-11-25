package ar.edu.utn.frba.homeassistant.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.AutomationSceneCrossRef
import ar.edu.utn.frba.homeassistant.data.model.AutomationWithScenes

@Dao
interface AutomationDao {
    @Query("SELECT * FROM Automation")
    fun getAll(): LiveData<List<Automation>>

    @Transaction
    @Query("SELECT * FROM Automation")
    fun getAllWithScenes(): LiveData<List<AutomationWithScenes>>

    @Insert
    suspend fun insert(automation: Automation): Long

    @Insert
    suspend fun insertAutomationSceneCrossRef(automationSceneCrossRef: AutomationSceneCrossRef)

    @Query("DELETE FROM AutomationSceneCrossRef WHERE automationId = :automationId")
    suspend fun deleteAutomationScenes(automationId: Long)

    @Update
    suspend fun update(automation: Automation)

    @Delete
    suspend fun delete(automation: Automation)
}
