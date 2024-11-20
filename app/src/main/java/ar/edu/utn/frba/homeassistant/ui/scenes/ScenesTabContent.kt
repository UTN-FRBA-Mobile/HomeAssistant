package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun ScenesTabContent(viewModel: ScenesViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val scenes by viewModel.scenes.observeAsState(emptyList())
    val devices by viewModel.devices.observeAsState(emptyList())

    NavHost(navController, startDestination = "scenesList") {
        composable("scenesList") {
            ScenesScreen(navController, scenes, viewModel::deleteScene, viewModel::toggleDevice, viewModel::toggleScene)
        }
        composable("sceneDetail/{sceneId}") { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getString("sceneId")?.toLong() ?: -1
            SceneDetailScreen(navController, scenes, sceneId, viewModel::updateScene)
        }
        composable("addScene") {
            AddSceneScreen(navController, { name, devices -> viewModel.addScene(name, devices) }, availableDevices = devices)
        }
    }
}
