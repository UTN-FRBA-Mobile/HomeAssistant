package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.SceneWithDevices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneDetailScreen(
    navController: NavHostController,
    scenes: List<SceneWithDevices>,
    sceneId: Long,
    onUpdateScene: (Long, List<Device>) -> Unit // Accepter deux paramètres
) {
    val sceneWithDevices = scenes.find { it.scene.sceneId == sceneId }

    if (sceneWithDevices == null ) {
        Text(
            stringResource(R.string.scene_not_found), modifier = Modifier.padding(16.dp))
        return
    }

    val scene = sceneWithDevices.scene
    val sceneDevices = sceneWithDevices.devices

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scene_details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
            Text(stringResource(R.string.scene_name)+": ${scene.name}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            sceneDevices.forEach { device ->
                DeviceToggleRow(device = device) { updatedDevice ->
                    val updatedDevices = sceneDevices.map {
                        if (it.deviceId == updatedDevice.deviceId) updatedDevice else it
                    }
                    onUpdateScene(scene.sceneId, updatedDevices)
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
