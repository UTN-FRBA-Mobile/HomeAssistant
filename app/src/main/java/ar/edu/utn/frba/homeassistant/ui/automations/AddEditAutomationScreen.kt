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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes
import ar.edu.utn.frba.homeassistant.data.model.Scene
import ar.edu.utn.frba.homeassistant.data.model.ShakeAutomation
import ar.edu.utn.frba.homeassistant.ui.automations.forms.ClockAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.GeolocationAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.ShakeAutomationForm
import ar.edu.utn.frba.homeassistant.ui.automations.forms.components.DropdownMenuComponent
import ar.edu.utn.frba.homeassistant.ui.automations.forms.components.MultiSelectDropdownComponent

@Composable
fun AddEditAutomationScreen(
    navController: NavHostController,
    onCreate: (IAutomation, Set<Scene>) -> Unit,
    scenes: List<Scene>,
    getCurrentCoordinates: GetCurrentCoordinates,
    automationId: Long = 0,
    onUpdate: (IAutomation, Set<Scene>, IAutomation, Set<Scene>) -> Unit = { _, _, _, _ -> },
    automations: List<IAutomationWithScenes> = emptyList(),
) {
    AutomationForm(
        goBack = { navController.popBackStack() },
        scenes = scenes,
        onCreate = onCreate,
        getCurrentCoordinates = getCurrentCoordinates,
        automationId,
        onUpdate,
        automations
    )
}

private const val CLOCK_AUTOMATION = "Clock Automation"

private const val GEOLOCATION_AUTOMATION = "Geolocation Automation"

private const val SHAKE_AUTOMATION = "Shake Automation"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationForm(
    goBack: () -> Unit,
    scenes: List<Scene>,
    onCreate: (IAutomation, Set<Scene>) -> Unit,
    getCurrentCoordinates: GetCurrentCoordinates,
    automationId: Long = 0,
    onUpdate: (IAutomation, Set<Scene>, IAutomation, Set<Scene>) -> Unit = { _, _, _, _ -> },
    automations: List<IAutomationWithScenes>,
) {
    val automation = automations.find { it.automation.automationId == automationId }
    val isEditMode = automation != null

    val initialSelectedAutomation = when (automation?.automation) {
        is ClockAutomation -> CLOCK_AUTOMATION
        is GeolocationAutomation -> GEOLOCATION_AUTOMATION
        is ShakeAutomation -> SHAKE_AUTOMATION
        else -> CLOCK_AUTOMATION
    }

    val initialSelectedScenes = automation?.scenes?.toSet() ?: setOf()

    var selectedAutomation by remember { mutableStateOf(initialSelectedAutomation) }
    var selectedScenes by remember { mutableStateOf(initialSelectedScenes) }

    val handleUpsert: (IAutomation) -> Unit = {
        if (isEditMode) {
            onUpdate(automation!!.automation, automation.scenes.toSet(), it, selectedScenes)
        } else {
            onCreate(it, selectedScenes)
        }
        goBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit automation" else stringResource(R.string.add_new_auto)) },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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

                DropdownMenuComponent(
                    selectedOption = selectedAutomation,
                    options = listOf(
                        CLOCK_AUTOMATION,
                        GEOLOCATION_AUTOMATION,
                        SHAKE_AUTOMATION
                    ),
                    onOptionSelected = { selectedAutomation = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isEditMode
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    CLOCK_AUTOMATION -> ClockAutomationForm(
                        selectedScenes,
                        handleUpsert,
                        automationId,
                        automation as ClockAutomationWithScenes?
                    )

                    GEOLOCATION_AUTOMATION -> GeolocationAutomationForm(
                        getCurrentCoordinates = getCurrentCoordinates,
                        selectedScenes,
                        handleUpsert,
                        automationId,
                        automation as GeolocationAutomationWithScenes?
                    )

                    SHAKE_AUTOMATION -> ShakeAutomationForm(
                        selectedScenes,
                        handleUpsert,
                        automationId
                    )
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
        onCreate = { _: IAutomation, _: Set<Scene> -> },
        getCurrentCoordinates = { },
        automations = emptyList()
    )
}