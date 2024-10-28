package ar.edu.utn.frba.homeassistant

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.homeassistant.ui.theme.HomeAssistantTheme
import ar.edu.utn.frba.homeassistant.utils.GeofenceBroadcastReceiver
import ar.edu.utn.frba.homeassistant.utils.registerShakeSensor
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var geofencingClient: GeofencingClient

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

        // SHAKING
        // This code is temporal for testing purposes
        val sensorManager: SensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        registerShakeSensor(sensorManager) {
            Toast.makeText(this, "Shake Detected", Toast.LENGTH_SHORT).show()
        }

        // LOCATION
        // https://developer.android.com/develop/sensors-and-location/location/permissions?hl=es-419
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d("GEO","Precise location access granted")
                }

                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.d("GEO","Approximate location access granted")
                }
            }
        }

        // LOCATION PERMISSIONS

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // https://developer.android.com/develop/sensors-and-location/location/permissions?hl=es-419#background-dialog-target-android-11
            locationPermissionRequest.launch(arrayOf(ACCESS_BACKGROUND_LOCATION))
        } else {
            // https://developer.android.com/develop/sensors-and-location/location/permissions?hl=es-419#background-dialog-target-android-10-or-lower
            // Background location access not needed
        }

        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GEO","Location permission granted")


        } else {
            Log.d("GEO","Location permission not granted")
        }

        // GEOFENCING
        // https://medium.com/@KaushalVasava/geofence-in-android-8add1f6b9be1
        geofencingClient = LocationServices.getGeofencingClient(this)
        val latitude = -34.598467238301744
        val longitude = -58.42012906027254
        val radius = 100f
        val geofence = Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId("Test")


            // Set the circular region of this geofence.
            .setCircularRegion(
                latitude,
                longitude,
                radius
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            //.setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // Create the geofence.
            .build()

        val geofenceRequest = GeofencingRequest.Builder().addGeofence(geofence).setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            val pendingIntent = PendingIntent.getBroadcast(this, 0, Intent(this, GeofenceBroadcastReceiver::class.java), PendingIntent.FLAG_MUTABLE)

            geofencingClient.addGeofences(geofenceRequest, pendingIntent).run {
                addOnSuccessListener {
                    Log.d("Geofence", "Geofence added")
                }
                addOnFailureListener { exception ->
                    Log.d("Geofence", "Geofence not added: ${exception}")
                }
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
                Text(
                    text = "Devices",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            composable("scenes") {
                Text(
                    text = "Scenes",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
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
