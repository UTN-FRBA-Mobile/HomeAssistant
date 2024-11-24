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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.homeassistant.R
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
                title = { Text(stringResource(R.string.my_scenes)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addScene")
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_scene))
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
                SceneItem(scene, onDelete = {
                    onDelete(scene.scene)
                }, onToggle = { device, isOn ->
                    onToggle(device, isOn)
                }, onToggleScene = { onToggleScene(scene, it) })
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}


@Composable
fun SceneItem(
    sceneWithDevices: SceneWithDevices,
    onDelete: () -> Unit,
    onToggle: (Device, Boolean) -> Unit,
    onToggleScene: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scene = sceneWithDevices.scene
    val sceneDevices = sceneWithDevices.devices
    var showDialog by remember { mutableStateOf(false) }

    println(sceneWithDevices)


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.delete_scene)) },
            text = { Text(stringResource(R.string.sure_to_delete_scene)) }
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
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = scene.name, style = MaterialTheme.typography.titleMedium)
            }
            Switch(
                checked = sceneDevices.all { it.isOn },
                onCheckedChange = {
                    onToggleScene(it)
                }
            )

            IconButton(onClick = {
                showDialog = true  // Show confirmation dialog
            }) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_scene))
            }
        }
        if (expanded) {
            sceneDevices.forEach { device ->
                DeviceControl(device, onToggle = { onToggle(device, it) })
            }
        }
    }
}

@Composable
fun DeviceControl(device: Device, onToggle: (Boolean) -> Unit) {
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
            checked = device.isOn,
            onCheckedChange = {
                onToggle(it)
            }
        )
    }
}
