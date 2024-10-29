package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.homeassistant.data.Device
import ar.edu.utn.frba.homeassistant.data.Scene
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ScenesViewModel : ViewModel() {

    private val _scenes = MutableStateFlow<List<Scene>>(emptyList())
    val scenes: StateFlow<List<Scene>> = _scenes

    init {
        // Initialisation avec quelques scènes par défaut
        _scenes.value = listOf(
            Scene("1", "Cine", listOf(
                Device("1", "Living Room Light", "Light", isOn = false),
                Device("2", "Kitchen Light", "Light", isOn = false),
                Device("3", "Bedroom Light", "Light", isOn = false)
            )),
            Scene("2", "Dîner", listOf(
                Device("4", "Dining Room Light", "Light", isOn = false),
                Device("5", "Music System", "Audio", isOn = false)
            ))
        )
    }

    // Fonction pour ajouter une nouvelle scène
    fun addScene(name: String, devices: List<Device>) {
        val newScene = Scene(id = (_scenes.value.size + 1).toString(), name = name, devices = devices)
        _scenes.update { it + newScene }
    }

    // Fonction pour supprimer une scène
    fun deleteScene(scene: Scene) {
        _scenes.update { scenes -> scenes.filter { it.id != scene.id } }
    }

    // Fonction pour mettre à jour une scène (par exemple, changer l'état des dispositifs)
    fun updateScene(sceneId: String, updatedDevices: List<Device>) {
        _scenes.update { scenes ->
            scenes.map { scene ->
                if (scene.id == sceneId) {
                    scene.copy(devices = updatedDevices)
                } else {
                    scene
                }
            }
        }
    }
}