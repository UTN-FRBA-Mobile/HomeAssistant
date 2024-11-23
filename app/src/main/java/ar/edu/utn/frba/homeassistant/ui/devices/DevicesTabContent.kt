package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun DevicesTabContent(viewModel: DevicesViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val devices by viewModel.devices.observeAsState(emptyList())

    NavHost(navController, startDestination = "devicesList") {
        composable("devicesList") {
            DevicesScreen(navController, devices, viewModel::deleteDevice, viewModel::toggleDevice)
        }
        composable("deviceDetail/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId")?.toLong() ?: -1
            DeviceDetailScreen(navController, devices, deviceId, viewModel::updateDevice)
        }
        composable("addDevice") {
            AddDeviceScreen(navController, viewModel::addDevice)
        }
    }
}
