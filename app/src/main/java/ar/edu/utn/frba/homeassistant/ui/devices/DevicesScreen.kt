package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.homeassistant.data.Device
import ar.edu.utn.frba.homeassistant.data.DeviceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(navController: NavController) {
    val deviceRepository = remember { DeviceRepository() }
    val devices = remember { deviceRepository.getDevices() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Devices") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Handle add new device action here
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Device")
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
                DeviceRow(device, navController)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DeviceRow(device: Device, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("deviceDetail/${device.id}")
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
            Text(text = device.type, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
