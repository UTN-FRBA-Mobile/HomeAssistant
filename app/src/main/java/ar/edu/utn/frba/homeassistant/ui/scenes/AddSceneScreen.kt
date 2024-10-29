package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.data.Device
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSceneScreen(
    navController: NavHostController,
    onCreate: (String, List<Device>) -> Unit,
    availableDevices: StateFlow<List<Device>>
) {
    var sceneName by remember { mutableStateOf("") }
    var selectedDevices by remember { mutableStateOf<List<Device>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Scene") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = sceneName,
                onValueChange = { sceneName = it },
                label = { Text("Scene Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Devices:")
            Spacer(modifier = Modifier.height(8.dp))

            // List of available devices with buttons to toggle selection
            availableDevices.value.forEach { device ->
                val isSelected = selectedDevices.contains(device)

                OutlinedButton(
                    onClick = {
                        selectedDevices = if (isSelected) {
                            selectedDevices - device
                        } else {
                            selectedDevices + device
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = if (isSelected) "Remove ${device.name}" else "Add ${device.name}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (sceneName.isNotBlank()) {
                        onCreate(sceneName, selectedDevices)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Scene")
            }
        }
    }
}