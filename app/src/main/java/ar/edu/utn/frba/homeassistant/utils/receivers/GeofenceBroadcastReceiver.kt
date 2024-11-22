package ar.edu.utn.frba.homeassistant.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.network.DEVICE_IDS
import ar.edu.utn.frba.homeassistant.network.UdpService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent.fromIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "$GLOBAL_TAG#GEOFENCE_BROADCAST_RECEIVER";

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "[onReceive]: Geofence broadcast received")
        val geofencingEvent = fromIntent(intent)
        val id = intent.getLongExtra("automationId", -1)
        val devices = intent.getLongArrayExtra(DEVICE_IDS)
        val turnOnWhenEntering = intent.getBooleanExtra("turnOnWhenEntering", true)
        Log.d(TAG, "[onReceive]: Geofence broadcast received for automation with id: $id")

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, "[onReceive]: Geofence error: $errorMessage")
                return
            }
        }

        // Manejar las transiciones de entrada o salida
        val geofenceTransition = geofencingEvent?.geofenceTransition
        Log.d(TAG, "[onReceive]: GeofenceEvent: $geofencingEvent")
        Log.d(TAG, "[onReceive]: Geofence transition: $geofenceTransition")
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                devices?.forEach {
                    if (turnOnWhenEntering) {
                        Log.d(TAG, "[onReceive]: Sending UDP message to device $it - toggle:on")
                        CoroutineScope(Dispatchers.IO).launch {
                            UdpService.sendUdpMessage(it, "toggle:on")
                        }
                    } else {
                        Log.d(TAG, "[onReceive]: Sending UDP message to device $it - toggle:off")
                        CoroutineScope(Dispatchers.IO).launch {
                            UdpService.sendUdpMessage(it, "toggle:off")
                        }
                    }
                }
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                devices?.forEach {
                    if (turnOnWhenEntering) {
                        Log.d(TAG, "[onReceive]: Sending UDP message to device $it - toggle:off")
                        CoroutineScope(Dispatchers.IO).launch {
                            UdpService.sendUdpMessage(it, "toggle:off")
                        }
                    } else {
                        Log.d(TAG, "[onReceive]: Sending UDP message to device $it - toggle:on")
                        CoroutineScope(Dispatchers.IO).launch {
                            UdpService.sendUdpMessage(it, "toggle:on")
                        }
                    }
                }
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i(TAG, "[onReceive]: Geofence transition dwell - no action")
                // Toast.makeText(context.applicationContext, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.e("Geofence", "Geofence transition error")
            }
        }
    }
}