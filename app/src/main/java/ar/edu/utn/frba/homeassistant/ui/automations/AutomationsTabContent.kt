package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates

@Composable
fun AutomationsTabContent(
    viewModel: AutomationsViewModel = hiltViewModel(),
    getCurrentCoordinates: GetCurrentCoordinates
) {
    val navController = rememberNavController()
    val automationsWithScenes by viewModel.automationsWithScenes.observeAsState(emptyList())
    val scenes by viewModel.scenes.observeAsState(emptyList())

    NavHost(navController, startDestination = "automationsList") {
        composable("addAutomation") {
            AddEditAutomationScreen(
                navController,
                viewModel::addAutomation,
                scenes = scenes,
                getCurrentCoordinates
            )
        }
        composable("editAutomation/{automationId}") { backStackEntry ->
            val automationId = backStackEntry.arguments?.getString("automationId")?.toLong() ?: 0
            AddEditAutomationScreen(
                navController,
                viewModel::addAutomation,
                scenes = scenes,
                getCurrentCoordinates,
                automationId,
                viewModel::updateAutomation,
                automationsWithScenes
            )
        }
        composable("automationsList") {
            AutomationsScreen(
                navController,
                automationsWithScenes,
                viewModel::deleteAutomation,
                viewModel::toggleAutomation
            )
        }
    }
}