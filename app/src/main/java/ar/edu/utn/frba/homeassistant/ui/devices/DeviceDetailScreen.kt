package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.data.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(navController: NavHostController, devices: List<Device>, deviceId: String) {
    val device = devices.find { it.id == deviceId }

    if (device == null) {
        Text("Device not found", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Details") },
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
            Text("Device Name: ${device.name}", style = MaterialTheme.typography.titleLarge)
            Text("Device Type: ${device.type}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Handle device action, e.g., toggle on/off
            }) {
                Text("Toggle Power")
            }
        }
    }
}
