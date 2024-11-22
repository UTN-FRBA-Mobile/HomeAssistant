package ar.edu.utn.frba.homeassistant

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.homeassistant.network.ShakeAutomationForegroundService
import ar.edu.utn.frba.homeassistant.ui.SnackbarManager
import ar.edu.utn.frba.homeassistant.ui.automations.AutomationsTabContent
import ar.edu.utn.frba.homeassistant.ui.devices.DevicesTabContent
import ar.edu.utn.frba.homeassistant.ui.scenes.ScenesTabContent
import ar.edu.utn.frba.homeassistant.ui.theme.HomeAssistantTheme
import ar.edu.utn.frba.homeassistant.utils.requestLocationPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

typealias GetCurrentCoordinates = (onSuccess: (Double, Double) -> Unit) -> Unit

const val GLOBAL_TAG = "HOME_ASSISTANT_TRACE"
const val TAG = "$GLOBAL_TAG#MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val alarmManager = this.getSystemService(AlarmManager::class.java)


        Log.d(TAG, "[onCreate]: Checking if we can schedule exact alarms")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when {
                // If permission is granted, proceed with scheduling exact alarms.
                alarmManager.canScheduleExactAlarms() -> {
                    Log.d(TAG, "[onCreate]: We can schedule exact alarms")
                } else -> {
                    Log.d(TAG, "[onCreate]: We can't schedule exact alarms, asking user to go to settings")
                    // Ask users to go to exact alarm page in system settings.
                    this.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO: ATM you will need to activate notifications manually. We should add an alert.
//            val channelId = "ALARM_AUTOMATIONS"
//            val channelName = "Automations Notifications"
//            val channel =
//                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "[onCreate]: Creating Shake Automation Foreground Service channel")
            val udpChannel = NotificationChannel(
                "SHAKE_SERVICE_CHANNEL",
                "Shake Automation Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(udpChannel)
        } else {
            Log.d(TAG, "[onCreate]: Not creating channel because we are not in Oreo")
        }

        Log.d(TAG, "[onCreate]: Starting Shake Automation Foreground Service")
        val intent = Intent(
            this,
            ShakeAutomationForegroundService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "[onCreate]: Starting Shake Automation Foreground Service with startForegroundService")
            this.startForegroundService(
                intent
            )
        } else {
            Log.d(TAG, "[onCreate]: Starting Shake Automation Foreground Service with startService")
            this.startService(intent)
        }

        fun getCurrentCoordinates(onSuccess: (Double, Double) -> Unit) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        Log.i(TAG, "[Location]: ${location.latitude}, ${location.longitude}")
                        onSuccess(location.latitude, location.longitude)
                    }
                }

            }
        }

        setContent {
            // Theme created by following this guide: https://developer.android.com/develop/ui/compose/designsystems/material3?hl=es-419
            // Theme Related files were autogenerated by using this tool: https://material-foundation.github.io/material-theme-builder/
            HomeAssistantTheme {
                HomeAssistantMainScaffold(::getCurrentCoordinates)
            }
        }

        // LOCATION
        requestLocationPermissions(this)
    }
}

@Preview
// How to make a simple Scaffold: https://developer.android.com/develop/ui/compose/components/scaffold?hl=es-419
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAssistantMainScaffold(getCurrentCoordinates: GetCurrentCoordinates = {}) {

    // I put this line here so that we can use @Preview while developing
    // It might be a good idea to move this above HomeAssistantTheme in setContent function
    // As shown in: https://medium.com/@jpmtech/jetpack-compose-bottom-navigation-bar-3e1e8749fb2c
    val navController = rememberNavController()

    Scaffold(
        snackbarHost = { SnackbarHost(SnackbarManager.snackbarHostState) },
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
                AutomationsTabContent(getCurrentCoordinates = getCurrentCoordinates)
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
