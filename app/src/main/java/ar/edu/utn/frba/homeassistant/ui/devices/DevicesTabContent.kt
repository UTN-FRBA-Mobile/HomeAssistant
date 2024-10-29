package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun DevicesTabContent(viewModel: DevicesViewModel = viewModel()) {
    val navController = rememberNavController()
    val devices by DevicesViewModel.devices.collectAsStateWithLifecycle()


    NavHost(navController, startDestination = "devicesList") {
        composable("devicesList") {
            DevicesScreen(navController, devices, viewModel::deleteDevice)
        }
        composable("deviceDetail/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            DeviceDetailScreen(navController, devices, deviceId)
        }
        composable("addDevice") {
            AddDeviceScreen(navController, viewModel::addDevice)
        }
    }
}
