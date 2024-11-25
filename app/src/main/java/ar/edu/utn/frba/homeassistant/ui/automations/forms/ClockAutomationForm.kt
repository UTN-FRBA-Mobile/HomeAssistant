package ar.edu.utn.frba.homeassistant.ui.automations.forms

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.Automation
import ar.edu.utn.frba.homeassistant.data.model.AutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.CLOCK_AUTOMATION
import ar.edu.utn.frba.homeassistant.data.model.Scene
import java.util.Calendar

@Composable
fun ClockAutomationForm(
    selectedScenes: Set<Scene>,
    onCreate: (Automation) -> Unit,
    automationId: Long,
    automationWithScenes: AutomationWithScenes?
) {
    val automation = automationWithScenes?.automation

    var time by remember { mutableStateOf(automation?.time ?: "") }
    var shouldTurnOn by remember { mutableStateOf(automation?.shouldTurnOn ?: true) }

    val selectedDays = remember {
        mutableStateMapOf( // Estado para días seleccionados
            "Monday" to (automation?.monday ?: false),
            "Tuesday" to (automation?.tuesday ?: false),
            "Wednesday" to (automation?.wednesday ?: false),
            "Thursday" to (automation?.thursday ?: false),
            "Friday" to (automation?.friday ?: false),
            "Saturday" to (automation?.saturday ?: false),
            "Sunday" to (automation?.sunday ?: false)
        )
    }

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

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val showTimePicker: (onTimeSelected: (String) -> Unit) -> Unit = { onTimeSelected ->
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                onTimeSelected(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()) // TODO: Fix!
            .fillMaxHeight()
    ) {
        Column {
            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text(stringResource(R.string.trigger_time)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showTimePicker { time = it } }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_start)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = shouldTurnOn,
                    onCheckedChange = { shouldTurnOn = it }
                )
                Text(text = stringResource(R.string.should_turn_on))
            }

            Text(text = stringResource(R.string.select_days))

            // Grid layout for days of the week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    selectedDays.keys.filterIndexed { index, _ -> index % 2 == 0 }
                        .forEachIndexed { index, day ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedDays[day] ?: false,
                                    onCheckedChange = { selectedDays[day] = it }
                                )
                                Text(text = day)
                            }
                        }
                }
                Column(modifier = Modifier.weight(1f)) {
                    selectedDays.keys.filterIndexed { index, _ -> index % 2 != 0 }
                        .forEachIndexed { index, day ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedDays[day] ?: false,
                                    onCheckedChange = { selectedDays[day] = it }
                                )
                                Text(text = day)
                            }
                        }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (selectedScenes.isEmpty()) {
                        showAlert = true
                    } else {

                        val name = selectedDays.entries.filter { it.value }
                            .map { it.key.slice(IntRange(0, 1)) }.joinToString(
                                ", "
                            )
                        val newAutomation = Automation(
                            automationId = automationId,
                            time = time,
                            isOn = false,
                            name = "[${name}] - ${time}",
                            enabled = true,
                            shouldTurnOn = shouldTurnOn,
                            monday = selectedDays["Monday"] ?: false,
                            tuesday = selectedDays["Tuesday"] ?: false,
                            wednesday = selectedDays["Wednesday"] ?: false,
                            thursday = selectedDays["Thursday"] ?: false,
                            friday = selectedDays["Friday"] ?: false,
                            saturday = selectedDays["Saturday"] ?: false,
                            sunday = selectedDays["Sunday"] ?: false,
                            type = CLOCK_AUTOMATION
                        )
                        onCreate(newAutomation)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}