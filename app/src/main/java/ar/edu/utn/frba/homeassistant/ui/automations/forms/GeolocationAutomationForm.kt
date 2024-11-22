package ar.edu.utn.frba.homeassistant.ui.automations.forms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.utils.requestLocationPermissions
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun GeolocationAutomationForm(
    getCurrentCoordinates: GetCurrentCoordinates,
    selectedScenes : Set<Scene>,
    onCreate: (IAutomation) -> Unit
) {
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    var showAlert by remember { mutableStateOf(false) }
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            confirmButton = {
                if(false) {
                    Button(onClick = {
                        showAlert = false
                    }) {
                        Text("do anyway")
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showAlert = false }) {
                    Text("Ok")
                }
            },
            title = { Text("Action impossible") },
            text = { Text("You didn't select any scene. You have to select al menos una.") }
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

            Button(
                onClick = {
                    getCurrentCoordinates { lat, long ->
                        latitude = lat
                        longitude = long
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Use Current Location")
            }
        }

        Column(
        ) {
            Button(
                onClick = {
                    if (selectedScenes.isEmpty()) {
                        showAlert = true
                    }else {
                        val automation: GeolocationAutomation = GeolocationAutomation(
                            automationId = null,
                            latitude = latitude,
                            longitude = longitude,
                            name = "${latitude}, ${longitude}"
                        )

                        onCreate(automation as IAutomation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }


}