package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    navController: NavController,
    devices: List<Device>,
    onDelete: (Device) -> Unit,
    onToggle: (Device, Boolean) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_devices)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addDevice")
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_device))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(devices.size) { index ->
                val device = devices[index]
                DeviceRow(device, navController, onDelete = {
                    onDelete(device)
                }, onToggle = { onToggle(device, it)  })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DeviceRow(
    device: Device,
    navController: NavController,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

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
            title = { Text(stringResource(R.string.delete_device)) },
            text = { Text(stringResource(R.string.sure_to_delete_dev)) }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .clickable {
                navController.navigate("deviceDetail/${device.deviceId}")
            }
        ) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
            Text(text = device.type, style = MaterialTheme.typography.bodyMedium)
        }

        Switch(
            checked = device.isOn,
            onCheckedChange = {
                onToggle(it)
            }
        )

        IconButton(onClick = {
            showDialog = true  // Show confirmation dialog
        }) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_device))
        }
    }
}
