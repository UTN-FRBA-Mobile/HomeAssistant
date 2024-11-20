package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.homeassistant.data.model.Device
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.SceneWithDevices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenesScreen(
    navController: NavController,
    scenes: List<SceneWithDevices>,
    onDelete: (Scene) -> Unit,
    onToggle: (Device, Boolean) -> Unit,
    onToggleScene: (SceneWithDevices, Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Scenes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addScene")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Scene")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(scenes.size) { index ->
                val scene = scenes[index]
                SceneItem(scene, navController, onDelete = {
                    onDelete(scene.scene)
                }, onToggle = { device, isOn ->
                    onToggle(device, isOn)
                }, onToggleScene = { onToggleScene(scene, it)  })
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}


@Composable
fun SceneItem(sceneWithDevices: SceneWithDevices, navController: NavController, onDelete: () -> Unit, onToggle: (Device, Boolean) -> Unit, onToggleScene: (Boolean) -> Unit) {
    var isOn by remember { mutableStateOf(sceneWithDevices.isOn) }
    var expanded by remember { mutableStateOf(false) }
    val scene = sceneWithDevices.scene
    val sceneDevices = sceneWithDevices.devices
    var showDialog by remember { mutableStateOf(false) }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Scene") },
            text = { Text("Are you sure you want to delete this scene?") }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier
                .weight(1f)
            ) {
                Text(text = scene.name, style = MaterialTheme.typography.titleMedium)
            }
            Switch(
                checked = isOn,
                onCheckedChange = {
                    isOn = it
                    onToggleScene(it)
                }
            )

            IconButton(onClick = {
                showDialog = true  // Show confirmation dialog
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Scene")
            }
        }
        if (expanded) {
            sceneDevices.forEach { device ->
                DeviceControl(device, onToggle = { onToggle(device, it)  })
            }
        }
    }
}

@Composable
fun DeviceControl(device: Device, onToggle: (Boolean) -> Unit) {
    var isOn by remember { mutableStateOf(device.isOn) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Text(
            text = device.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Switch(
            checked = isOn,
            onCheckedChange = {
                isOn = it
                onToggle(it)
            }
        )
    }
}
