package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun DevicesTabContent() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "devicesList") {
        composable("devicesList") {
            DevicesScreen(navController)
        }
        composable("deviceDetail/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            DeviceDetailScreen(navController, deviceId)
        }
    }
}
