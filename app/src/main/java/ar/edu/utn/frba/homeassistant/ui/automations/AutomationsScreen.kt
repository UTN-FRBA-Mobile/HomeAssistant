package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.model.IAutomation
import ar.edu.utn.frba.homeassistant.data.model.IAutomationWithScenes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationsScreen(
    navController: NavController,
    automations: List<IAutomationWithScenes>,
    onDelete: (IAutomation) -> Unit,
    onToggle: (IAutomationWithScenes, Boolean) -> Unit
) {

    println("AutomationsScreen: $automations")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.automations_label)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addAutomation")
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_automation)) // TODO: Use resources
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(automations.size) { index ->
                val automation = automations[index]
                AutomationRow(automation, navController, onDelete = {
                    onDelete(automation.automation)
                }, onToggle = { onToggle(automation, it)  })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun AutomationRow(
    automation: IAutomationWithScenes,
    navController: NavController,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.delete)+" "+ stringResource(R.string.automation)) },
            text = { Text(stringResource(R.string.sure_to_delete_auto)) }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .clickable {
                navController.navigate("automationDetail/${automation.automation.automationId}")
            }
        ) {
            Text(text = automation.automation.name, style = MaterialTheme.typography.titleMedium)
            Text(text = automation.scenes.joinToString { it.name }, style = MaterialTheme.typography.titleSmall)
        }

        Switch(
            checked = automation.automation.enabled,
            onCheckedChange = {
                onToggle(it)
            }
        )

        IconButton(onClick = {
            showDialog = true  // Show confirmation dialog
        }) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete)+" "+ stringResource(R.string.automation))
        }
    }
}