package ar.edu.utn.frba.homeassistant.ui.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.homeassistant.ui.devices.DevicesViewModel


@Composable
fun ScenesTabContent(viewModel: ScenesViewModel = viewModel()) {
    val navController = rememberNavController()
    val scenes by viewModel.scenes.collectAsStateWithLifecycle()

    NavHost(navController, startDestination = "scenesList") {
        composable("scenesList") {
            ScenesScreen(navController, scenes, viewModel::deleteScene)
        }
        composable("sceneDetail/{sceneId}") { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getString("sceneId") ?: ""
            SceneDetailScreen(navController, scenes, sceneId, viewModel::updateScene)
        }
        composable("addScene") {
            AddSceneScreen(navController, { name, devices -> viewModel.addScene(name, devices) }, availableDevices = DevicesViewModel.devices)
        }
    }
}
