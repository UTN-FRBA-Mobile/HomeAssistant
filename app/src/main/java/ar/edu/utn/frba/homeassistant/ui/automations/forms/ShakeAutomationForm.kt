package ar.edu.utn.frba.homeassistant.ui.automations.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation

@Composable
fun ShakeAutomationForm(
    selectedScenes : Set<Scene>,
    onCreate: (IAutomation) -> Unit,
) {
    var shakeIntensity by remember { mutableStateOf(0f) }

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
            text = { Text("You didn't select any scene. You have to select at least one.") }
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "Shake Intensity")
            Slider(
                value = shakeIntensity,
                onValueChange = { shakeIntensity = it },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = "Selected Intensity: ${shakeIntensity.toInt()}")
        }

        Column {
            Button(
                onClick = {
                    if (selectedScenes.isEmpty()) {
                        showAlert = true
                    }else {
                        val automation = ShakeAutomation(
                            automationId = null,
                            name = "Shake Automation ${shakeIntensity.toInt()}",
                            threshold = shakeIntensity.toInt()
                        )
                        onCreate(automation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}