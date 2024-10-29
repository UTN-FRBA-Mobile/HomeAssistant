package ar.edu.utn.frba.homeassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.homeassistant.ui.devices.DevicesTabContent
import ar.edu.utn.frba.homeassistant.ui.scenes.ScenesTabContent
import ar.edu.utn.frba.homeassistant.ui.theme.HomeAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // Theme created by following this guide: https://developer.android.com/develop/ui/compose/designsystems/material3?hl=es-419
            // Theme Related files were autogenerated by using this tool: https://material-foundation.github.io/material-theme-builder/
            HomeAssistantTheme {
                HomeAssistantMainScaffold()
            }
        }
    }
}

@Preview
// How to make a simple Scaffold: https://developer.android.com/develop/ui/compose/components/scaffold?hl=es-419
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAssistantMainScaffold() {

    // I put this line here so that we can use @Preview while developing
    // It might be a good idea to move this above HomeAssistantTheme in setContent function
    // As shown in: https://medium.com/@jpmtech/jetpack-compose-bottom-navigation-bar-3e1e8749fb2c
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text(text = "Home Assistant") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavigationItem(
                        ImageVector.vectorResource(id = R.drawable.devices),
                        R.string.devices_label, "devices"
                    ),
                    BottomNavigationItem(
                        ImageVector.vectorResource(id = R.drawable.scenes),
                        R.string.scenes_label,
                        "scenes"
                    ),
                    BottomNavigationItem(
                        Icons.Default.Settings,
                        R.string.automations_label,
                        "automations"
                    )
                ),
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "devices",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("devices") {
                DevicesTabContent()
            }
            composable("scenes") {
                ScenesTabContent()
            }
            composable("automations") {
                Text(
                    text = "Automations",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}

data class BottomNavigationItem(
    val icon: ImageVector,
    val labelResourceId: Int,
    val route: String
)


// View how to make a Bottom Navigation Bar:
// https://medium.com/@jpmtech/jetpack-compose-bottom-navigation-bar-3e1e8749fb2c
@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    navController: NavController,
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            val label = stringResource(item.labelResourceId)

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                selected = index == selectedTabIndex,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(item.route)
                }
            )
        }
    }
}
