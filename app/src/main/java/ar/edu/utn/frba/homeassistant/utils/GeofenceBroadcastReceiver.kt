package ar.edu.utn.frba.homeassistant.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent.fromIntent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e("Geofence", errorMessage)
                return
            }
        }

        // Manejar las transiciones de entrada o salida
        val geofenceTransition = geofencingEvent?.geofenceTransition
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i("Geofence", "Geofence ENTER detected")
                Toast.makeText(context.applicationContext, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.i("Geofence", "Geofence EXIT detected")
                Toast.makeText(context.applicationContext, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i("Geofence", "Geofence DWELL detected")
                Toast.makeText(context.applicationContext, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.e("Geofence", "Geofence transition error")
            }
        }
    }
}