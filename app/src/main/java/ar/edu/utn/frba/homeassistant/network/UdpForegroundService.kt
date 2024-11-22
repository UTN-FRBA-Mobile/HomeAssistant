package ar.edu.utn.frba.homeassistant.network

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.utils.Receivers.GeofenceBroadcastReceiver
import ar.edu.utn.frba.homeassistant.utils.buildGeofence
import ar.edu.utn.frba.homeassistant.utils.buildGeofenceRequest
import ar.edu.utn.frba.homeassistant.utils.registerShakeSensor
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val CLOCK_AUTOMATION = "CLOCK_AUTOMATION"
const val SHAKE_AUTOMATION = "SHAKE_AUTOMATION"
const val GEO_AUTOMATION = "GEO_AUTOMATION"

const val DEVICE_IDS = "DEVICE_IDS"

const val TAG = "${GLOBAL_TAG}#UDP_FOREGROUND_SERVICE"

class UdpForegroundServiceBroadcastReceiver :
    BroadcastReceiver() {
    private lateinit var sensorManager: SensorManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == null) {
            Log.d(TAG, "[onReceive]: No action received")
            return
        }
        Log.d(TAG, "[onReceive]: [$action] registration request received")
        val deviceIds = intent.getLongArrayExtra(DEVICE_IDS)
        Log.d(TAG, "[onReceive]: Registering Shake automation for devices: ${deviceIds?.contentToString()}")
        when (action) {
            SHAKE_AUTOMATION -> registerShakeAutomation(deviceIds)
            GEO_AUTOMATION -> registerGeolocationAutomation(context, intent, deviceIds)
            CLOCK_AUTOMATION -> registerClockAutomation()

        }
    }

    private fun registerClockAutomation() {
        TODO("Not yet implemented")
    }

    private fun registerGeolocationAutomation(
        context: Context?,
        intent: Intent,
        deviceIds: LongArray?
    ) {
        if (context == null) {
            Log.d(TAG, "[registerGeolocationAutomation]: Context is null")
            return
        }

        if (deviceIds == null || deviceIds.isEmpty()) {
            Log.d(TAG, "[registerGeolocationAutomation]: No device ids found")
            return
        }

        Log.d(TAG, "[registerGeolocationAutomation]: Registering geolocation automation")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val radius = intent.getFloatExtra("radius", 100f)
        val id = intent.getLongExtra("automationId", -1)

        if (latitude == 0.0 || longitude == 0.0 || id == -1L) {
            Log.d(TAG, "[registerGeolocationAutomation]: Invalid parameters found. Can't register geolocation automation. Received latitude: $latitude, longitude: $longitude, id: $id")
            return
        }

        Log.d(TAG, "[registerGeolocationAutomation]: Registering Geofence $id at $latitude, $longitude with radius $radius meters")

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
                    Log.d(TAG, "[registerGeolocationAutomation#addGeofences#onSuccessListener]: Geofence added successfully")
                }
                addOnFailureListener { exception ->
                    Log.e(TAG, "[registerGeolocationAutomation#addGeofences#onFailureListener]: Error adding geofence", exception)
                }
            }
        } else {
            // TODO: What should we do if we don't have permissions?
            Log.d(TAG, "[registerGeolocationAutomation]: No permissions to add geofences")
        }
    }


    private fun registerShakeAutomation(deviceIds: LongArray?) {
        registerShakeSensor(sensorManager) {
            CoroutineScope(Dispatchers.IO).launch {
                deviceIds?.forEach {
                    UdpService.sendUdpMessage(it, "toggle:on")
                }
            }
        }
    }

    fun setSensorManager(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
    }
}

const val TAG_FOREGROUND_SERVICE = "${GLOBAL_TAG}#UDP_FOREGROUND_SERVICE"
class UdpForegroundService : Service() {
    private lateinit var broadcastReceiver: UdpForegroundServiceBroadcastReceiver

    override fun onCreate() {
        Log.d(TAG_FOREGROUND_SERVICE, "[onCreate]: UDP Foreground Service created")
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(CLOCK_AUTOMATION)
            addAction(SHAKE_AUTOMATION)
            addAction(GEO_AUTOMATION)
        }

        val sensorManager: SensorManager =
            application.applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
        broadcastReceiver = UdpForegroundServiceBroadcastReceiver()
        broadcastReceiver.setSensorManager(sensorManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d(TAG_FOREGROUND_SERVICE, "[onCreate]: Registering broadcast receiver for versions >= TIRAMISU")
                registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
            } else {
                Log.wtf(TAG_FOREGROUND_SERVICE, "[onCreate]: Can't register broadcast receiver for versions < TIRAMISU. Not implemented yet")
            }
        } else {
            Log.wtf(TAG_FOREGROUND_SERVICE, "[onCreate]: Can't register broadcast receiver for versions < OREO. Not implemented yet")
        }

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG_FOREGROUND_SERVICE, "[onStartCommand]: UDP Foreground Service started")
        val notification = NotificationCompat.Builder(this, "UDP_CHANNEL")
            .setContentTitle("Home Assistant")
            .setContentText("Service running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_FOREGROUND_SERVICE, "[onDestroy]: UDP Foreground Service destroyed")
        unregisterReceiver(broadcastReceiver)
    }
}