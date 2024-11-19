package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.ui.automations.forms.ClockAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.GeolocationAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.ShakeAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.components.DropdownMenuComponent
import ar.edu.utn.frba.homeassistant.ui.automations.forms.components.MultiSelectDropdownComponent
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun AddAutomationScreen(
    navController: NavHostController,
    onCreate: (Set<Scene>) -> (IAutomation) -> Unit,
    scenes: List<Scene>,
    getCurrentCoordinates: GetCurrentCoordinates
) {
    val context = LocalContext.current

    AutomationForm(
        goBack = { navController.popBackStack() },
        scenes = scenes,
        onCreate = onCreate,
        getCurrentCoordinates = getCurrentCoordinates
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationForm(
    goBack: () -> Unit,
    scenes: List<Scene>,
    onCreate: (Set<Scene>) -> (IAutomation) -> Unit,
    getCurrentCoordinates: GetCurrentCoordinates
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
                    "Geolocation Automation" -> GeolocationAutomationForm(getCurrentCoordinates = getCurrentCoordinates, onCreate = onCreate(selectedScenes))
                    "Shake Automation" -> ShakeAutomationForm(onCreate = onCreate(selectedScenes))
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


@Preview
@Composable
fun AddAutomationScreenPreview() {
    AutomationForm(
        goBack = {},
        scenes = emptyList(),
        onCreate = {
            { }
        },
        getCurrentCoordinates = { }
    )
}