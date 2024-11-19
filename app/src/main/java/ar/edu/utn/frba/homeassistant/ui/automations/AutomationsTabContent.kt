package ar.edu.utn.frba.homeassistant.ui.automations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AutomationsTabContent(viewModel: AutomationsViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val automations by viewModel.automations.observeAsState(emptyList())
    val scenes by viewModel.scenes.observeAsState(emptyList())

    NavHost(navController, startDestination = "automationsList") {
//        composable("devicesList") {
//            DevicesScreen(navController, devices, viewModel::deleteDevice, viewModel::toggleDevice)
//        }
//        composable("deviceDetail/{deviceId}") { backStackEntry ->
//            val deviceId = backStackEntry.arguments?.getString("deviceId")?.toLong() ?: -1
//            DeviceDetailScreen(navController, devices, deviceId)
//        }
        composable("addAutomation") {
            AddAutomationScreen(navController, viewModel::addAutomation, scenes = scenes)
        }
        composable("automationsList") {
            AutomationsScreen(navController, automations, viewModel::deleteAutomation, viewModel::toggleAutomation)
        }
    }
}