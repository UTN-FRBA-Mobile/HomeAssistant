package ar.edu.utn.frba.homeassistant.ui.automations.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.SHAKE_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.Scene

@Composable
fun ShakeAutomationForm(
    selectedScenes: Set<Scene>,
    onCreate: (Automation) -> Unit,
    automationId: Long,
) {
//    var shakeIntensity by remember { mutableStateOf(0f) }

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
//            Removed because we are going to use a default value for the intensity
//            Text(text = stringResource(R.string.shake_intensity))
//            Slider(
//                value = shakeIntensity,
//                onValueChange = { shakeIntensity = it },
//                valueRange = 0f..10f,
//                steps = 9,
//                modifier = Modifier.fillMaxWidth()
//            )
//            Text(text = "Selected Intensity: ${shakeIntensity.toInt()}")
        }

        Column {
            Button(
                onClick = {
                    if (selectedScenes.isEmpty()) {
                        showAlert = true
                    } else {
                        val automation = Automation(
                            automationId = automationId,
                            name = "Shake Automation",
                            type = SHAKE_AUTOMATION,
                        )
                        onCreate(automation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}