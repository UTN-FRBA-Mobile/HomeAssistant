package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    val scenes = repository.getScenesWithDevices()
    val devices = repository.getDevices()

    // Fonction pour ajouter une nouvelle scène
    fun addScene(name: String, devices: List<Device>) {
        viewModelScope.launch {
            repository.addScene(name, devices)
        }
    }

    // Fonction pour supprimer une scène
    fun deleteScene(scene: Scene) {
        viewModelScope.launch {
            repository.deleteScene(scene)
        }
    }

    // Fonction pour mettre à jour une scène (par exemple, changer l'état des dispositifs)
    fun updateScene(sceneId: Long, updatedDevices: List<Device>) {
        viewModelScope.launch {
            repository.updateScene(sceneId, updatedDevices)
        }
    }
}