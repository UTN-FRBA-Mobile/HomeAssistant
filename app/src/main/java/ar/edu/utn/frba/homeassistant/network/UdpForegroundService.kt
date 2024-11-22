package ar.edu.utn.frba.homeassistant.network

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.utils.registerShakeSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val CLOCK_AUTOMATION = "CLOCK_AUTOMATION"
const val SHAKE_AUTOMATION = "SHAKE_AUTOMATION"
const val GEO_AUTOMATION = "GEO_AUTOMATION"

const val DEVICE_IDS = "DEVICE_IDS"

class UdpForegroundServiceBroadcastReceiver :
    BroadcastReceiver() {
    private lateinit var sensorManager: SensorManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        println("Received action: $action")
        when (action) {
            SHAKE_AUTOMATION -> {
                val deviceIds = intent.getLongArrayExtra(DEVICE_IDS)
                registerShakeAutomation(deviceIds)
            }
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

class UdpForegroundService : Service() {
    private lateinit var broadcastReceiver: UdpForegroundServiceBroadcastReceiver

    override fun onCreate() {
        println("UdpForegroundService started")
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
                println("registrando broadcast")
                registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
            } else {
                println("No registre nada")
            }
        } else {
            println("No registre nada 2")
        }

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service started")
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
        unregisterReceiver(broadcastReceiver)
    }
}