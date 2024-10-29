package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.data.Scene
import ar.edu.utn.frba.homeassistant.data.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneDetailScreen(
    navController: NavHostController,
    scenes: List<Scene>,
    sceneId: String,
    onUpdateScene: (String, List<Device>) -> Unit // Accepter deux paramètres
) {
    val scene = scenes.find { it.id == sceneId }

    if (scene == null) {
        Text("Scene not found", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scene Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Scene Name: ${scene.name}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            scene.devices.forEach { device ->
                DeviceToggleRow(device = device) { updatedDevice ->
                    val updatedScene = scene.copy(
                        devices = scene.devices.map {
                            if (it.id == updatedDevice.id) updatedDevice else it
                        }
                    )
                    onUpdateScene(updatedScene.id, updatedScene.devices)
                }
            }
        }
    }
}

@Composable
fun DeviceToggleRow(device: Device, onToggle: (Device) -> Unit) {
    var isOn by remember { mutableStateOf(device.isOn) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = device.name, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = isOn,
            onCheckedChange = {
                isOn = it
                onToggle(device.copy(isOn = isOn))
            }
        )
    }
}
