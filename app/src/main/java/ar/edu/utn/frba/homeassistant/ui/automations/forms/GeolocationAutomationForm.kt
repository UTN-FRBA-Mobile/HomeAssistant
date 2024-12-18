package ar.edu.utn.frba.homeassistant.ui.automations.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.AutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.GEOLOCATION_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.Scene

@Composable
fun GeolocationAutomationForm(
    getCurrentCoordinates: GetCurrentCoordinates,
    selectedScenes: Set<Scene>,
    onCreate: (Automation) -> Unit,
    automationId: Long,
    automationWithScenes: AutomationWithScenes?
) {
    val automation = automationWithScenes?.automation

    var name by remember { mutableStateOf(automation?.name ?: "") }
    var isNameValid by remember { mutableStateOf(true) }
    var latitude by remember { mutableDoubleStateOf(automation?.latitude ?: 0.0) }
    var longitude by remember { mutableDoubleStateOf(automation?.longitude ?: 0.0) }
    var radius by remember { mutableFloatStateOf(automation?.radius ?: 100f) }

    var showAlert by remember { mutableStateOf(false) }
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            confirmButton = {
                if (false) {
                    Button(onClick = {
                        showAlert = false
                    }) {
                        Text(stringResource(R.string.do_anyway))
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showAlert = false }) {
                    Text("Ok")
                }
            },
            title = { Text(stringResource(R.string.action_impossible)) },
            text = { Text(stringResource(R.string.you_didnt_select)) }
        )
    }


    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                isError = !isNameValid,
                supportingText = {
                    if (!isNameValid) {
                        Text(stringResource(R.string.name_not_empty))
                    }
                }
            )

            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = { latitude = it.toDouble() },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = { longitude = it.toDouble() },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = radius.toString(),
                onValueChange = { radius = it.toFloat() },
                label = { Text("Radius") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    getCurrentCoordinates { lat, long ->
                        latitude = lat
                        longitude = long
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.use_current_loc))
            }
        }

        Column(
        ) {
            Button(
                onClick = {
                    if (selectedScenes.isEmpty()) {
                        showAlert = true
                    } else if (name.isBlank() ) {
                        isNameValid = false
                    } else {
                        val newAutomation = Automation(
                            automationId = automationId,
                            latitude = latitude,
                            longitude = longitude,
                            name = name,
                            radius = radius,
                            type = GEOLOCATION_AUTOMATION
                        )

                        onCreate(newAutomation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }


}