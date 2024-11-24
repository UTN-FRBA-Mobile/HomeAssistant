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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.R
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
        Text(
            stringResource(R.string.device_not_found), modifier = Modifier.padding(16.dp))
        return
    }

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(TextFieldValue(device.name)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.device_details)) },
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                if (isEditingName) {
                    Text(text = stringResource(R.string.device_name) , style = MaterialTheme.typography.bodyMedium)
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
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.confirm))
                    }
                    IconButton(onClick = {
                        isEditingName = false
                        editedName = TextFieldValue(device.name)
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cancel))
                    }
                } else {
                    Text(text = "Name: ${device.name}", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.type)+": ${device.type}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
