package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.data.model.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    navController: NavHostController,
    devices: List<Device>,
    deviceId: Long,
    updateDevice: (Device) -> Unit
) {
    val device = devices.find { it.deviceId == deviceId }

    if (device == null) {
        Text("Device not found", modifier = Modifier.padding(16.dp))
        return
    }

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(TextFieldValue(device.name)) }

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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                if (isEditingName) {
                    Text(text = "Name: ", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        isEditingName = false
                        updateDevice(device.copy(name = editedName.text))
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "Confirm")
                    }
                    IconButton(onClick = {
                        isEditingName = false
                        editedName = TextFieldValue(device.name)
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel")
                    }
                } else {
                    Text(text = "Name: ${device.name}", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Type: ${device.type}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
