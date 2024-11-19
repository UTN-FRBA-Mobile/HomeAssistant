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
import androidx.compose.ui.unit.dp
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import java.util.Calendar

@Composable
fun ClockAutomationForm(
    onCreate: (IAutomation) -> Unit
) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    val selectedDays = remember {
        mutableStateMapOf( // Estado para días seleccionados
            "Monday" to false,
            "Tuesday" to false,
            "Wednesday" to false,
            "Thursday" to false,
            "Friday" to false,
            "Saturday" to false,
            "Sunday" to false
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
                value = startTime,
                onValueChange = { },
                label = { Text("Start Time") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showTimePicker { startTime = it } }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Start Time")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = endTime,
                onValueChange = { },
                label = { Text("End Time") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showTimePicker { endTime = it } }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select End Time")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Days of the Week")

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

                    val name = selectedDays.entries.filter { it.value }
                        .map { it.key.slice(IntRange(0, 1)) }.joinToString(
                        ", "
                    )
                    val automation = ClockAutomation(
                        automationId = null,
                        timeTurnOn = startTime,
                        timeTurnOff = endTime,
                        isOn = false,
                        name = "[${name}] - ${startTime} - ${endTime}",
                        enabled = true,
                    )
                    onCreate(automation as IAutomation)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}