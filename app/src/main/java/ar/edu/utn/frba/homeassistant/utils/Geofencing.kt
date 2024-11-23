package ar.edu.utn.frba.homeassistant.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.data.model.GeolocationAutomation
import ar.edu.utn.frba.homeassistant.service.DEVICE_IDS
import ar.edu.utn.frba.homeassistant.utils.receivers.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

fun buildGeofence(requestId: String, latitude: Double, longitude: Double, radius: Float): Geofence {
    return Geofence.Builder()
        // Set the request ID of the geofence. This is a string to identify this
        // geofence.
        .setRequestId(requestId)


        // Set the circular region of this geofence.
        .setCircularRegion(
            latitude,
            longitude,
            radius
        )

        // Set the expiration duration of the geofence. This geofence gets automatically
        // removed after this period of time.
        .setExpirationDuration(Geofence.NEVER_EXPIRE)

        // Set the transition types of interest. Alerts are only generated for these
        // transition. We track entry and exit transitions in this sample.
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

        // Create the geofence.
        .build()
}

fun buildGeofenceRequest(geofence: Geofence): GeofencingRequest {
    return GeofencingRequest.Builder().addGeofence(geofence).setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()
}

const val GEOFENCE_TAG = "$GLOBAL_TAG#GEOFENCING"
fun registerGeofenceReceiver(context: Context, deviceIds: LongArray, automation: GeolocationAutomation){
    if (deviceIds.isEmpty()) {
        Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver]: No device ids found")
        return
    }

    Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver]: Registering geolocation automation")
    val latitude = automation.latitude
    val longitude = automation.longitude
    val radius = /*automation.radius*/ 100f
    val id = automation.automationId

    if (latitude == 0.0 || longitude == 0.0) {
        Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver]: Invalid parameters found. Can't register geolocation automation. Received latitude: $latitude, longitude: $longitude, id: $id")
        return
    }

    Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver]: Registering Geofence $id at $latitude, $longitude with radius $radius meters")

    // https://medium.com/@KaushalVasava/geofence-in-android-8add1f6b9be1
    val geofencingClient = LocationServices.getGeofencingClient(context)
    val geofence = buildGeofence("$latitude,$longitude", latitude, longitude, radius)
    val geofenceRequest = buildGeofenceRequest(geofence)
    val geofenceBroadcastIntent = Intent(context, GeofenceBroadcastReceiver::class.java)
    geofenceBroadcastIntent.putExtra("automationId", id)
    geofenceBroadcastIntent.putExtra(DEVICE_IDS, deviceIds)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        geofenceBroadcastIntent,
        PendingIntent.FLAG_MUTABLE
    )

    // It must be there or linter will fail.
    if (ActivityCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        geofencingClient.addGeofences(geofenceRequest, pendingIntent).run {
            addOnSuccessListener {
                Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver#addGeofences#onSuccessListener]: Geofence added successfully")
            }
            addOnFailureListener { exception ->
                Log.e(GEOFENCE_TAG, "[registerGeofenceReceiver#addGeofences#onFailureListener]: Error adding geofence", exception)
            }
        }
    } else {
        // TODO: What should we do if we don't have permissions?
        Log.d(GEOFENCE_TAG, "[registerGeofenceReceiver]: No permissions to add geofences")
    }
}
