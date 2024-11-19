package ar.edu.utn.frba.homeassistant.utils

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.

fun hasFineLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

// LOCATION
// https://developer.android.com/develop/sensors-and-location/location/permissions?hl=es-419
fun requestLocationPermissions(context: ComponentActivity) {
    val locationPermissionRequest = context.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                Log.d("GEO", "Precise location access granted")
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Log.d("GEO", "Approximate location access granted")
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
    if (hasFineLocationPermission(context)) {
        Log.d("GEO", "Location permission granted")
    } else {
        Log.d("GEO", "Location permission not granted")
    }
}