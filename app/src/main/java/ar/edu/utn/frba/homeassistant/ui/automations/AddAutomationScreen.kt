package ar.edu.utn.frba.homeassistant.ui.automations

import android.app.TimePickerDialog
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene
import java.util.Calendar

@Composable
fun AddAutomationScreen(
    navController: NavHostController,
    onCreate: (Set<Scene>) -> (IAutomation) -> Unit,
    scenes: List<Scene>
) {
    val context = LocalContext.current

    AutomationForm(
        goBack = { navController.popBackStack() },
        scenes = scenes,
        onCreate = onCreate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationForm(
    goBack: () -> Unit,
    scenes: List<Scene>,
    onCreate: (Set<Scene>) -> (IAutomation) -> Unit
) {
    var selectedAutomation by remember { mutableStateOf("Clock Automation") }
    var selectedScenes by remember { mutableStateOf(setOf<Scene>()) } // Estado para escenas seleccionadas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add new automation") },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {

                // Dropdown for selecting automation type
                DropdownMenuComponent(
                    selectedOption = selectedAutomation,
                    options = listOf(
                        "Clock Automation",
                        "Geolocation Automation",
                        "Shake Automation"
                    ),
                    onOptionSelected = { selectedAutomation = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for selecting scenes
                MultiSelectDropdownComponent<Scene>(
                    options = scenes,
                    selectedOptions = selectedScenes,
                    onOptionSelected = { selectedScenes = it },
                    modifier = Modifier.fillMaxWidth(),
                    getOptionLabel = { it.name }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display fields based on selected automation
                when (selectedAutomation) {
                    "Clock Automation" -> ClockAutomationForm(onCreate(selectedScenes))
                    "Geolocation Automation" -> GeolocationAutomationForm()
                    "Shake Automation" -> ShakeAutomationForm()
                }
            }
//            Column(
//                modifier = Modifier
//                    .padding(paddingValues)
//            ) {
//                Button(
//                    onClick = { /* Save logic */ },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(text = "Save")
//                }
//            }
        }
    }
}

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

                    val name = selectedDays.entries.filter { it.value }.map { it.key.slice(IntRange(0, 1)) }.joinToString(
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


@Composable
fun GeolocationAutomationForm() {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
        ) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Auto-set coordinates logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Use Current Location")
            }
        }

        Column(
        ) {
            Button(
                onClick = { /* Save logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }


}

@Composable
fun ShakeAutomationForm() {
    var shakeIntensity by remember { mutableStateOf(0f) }

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
                onClick = { /* Save logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun DropdownMenuComponent(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Box para controlar la posición del DropdownMenu
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text("Select Automation Type") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Menu")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = option, modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun <T> MultiSelectDropdownComponent(
    options: List<T>,
    selectedOptions: Set<T>,
    onOptionSelected: (Set<T>) -> Unit,
    modifier: Modifier = Modifier,
    getOptionLabel: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    println("Selected options: $options")

    Box(modifier = modifier) {
        OutlinedTextField(
            value = if (selectedOptions.isEmpty()) "Select Scenes" else selectedOptions.joinToString(
                ", ", transform = getOptionLabel
            ),
            onValueChange = { },
            label = { Text("Scenes") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Menu")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                DropdownMenuItem(
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onOptionSelected(newSelection)
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // Prevents event bubbling
                            )
                            Text(text = getOptionLabel(option))
                        }
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun AddAutomationScreenPreview() {
    AutomationForm(
        goBack = {},
        scenes = emptyList(),
        onCreate = {
            { }
        }
    )
}