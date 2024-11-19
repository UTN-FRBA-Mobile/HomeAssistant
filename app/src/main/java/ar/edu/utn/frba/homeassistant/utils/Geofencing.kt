package ar.edu.utn.frba.homeassistant.utils

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

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