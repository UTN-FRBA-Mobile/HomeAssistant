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
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene

@Composable
fun GeolocationAutomationForm(
    getCurrentCoordinates: GetCurrentCoordinates,
    selectedScenes: Set<Scene>,
    onCreate: (IAutomation) -> Unit,
    automationId: Long,
    automationWithScenes: GeolocationAutomationWithScenes?
) {
    val automation = automationWithScenes?.automation

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
        Column(
        ) {
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
                    } else {
                        val newAutomation = GeolocationAutomation(
                            automationId = automationId,
                            latitude = latitude,
                            longitude = longitude,
                            name = "$latitude, $longitude",
                            radius = radius,
                        )

                        onCreate(newAutomation as IAutomation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }


}