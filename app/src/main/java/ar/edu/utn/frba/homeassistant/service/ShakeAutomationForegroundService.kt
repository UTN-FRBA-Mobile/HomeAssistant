package ar.edu.utn.frba.homeassistant.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.network.UdpService
import ar.edu.utn.frba.homeassistant.utils.ShakeEventListener
import ar.edu.utn.frba.homeassistant.utils.registerShakeSensor
import ar.edu.utn.frba.homeassistant.utils.unregisterShakeSensor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val REGISTER_SHAKE_AUTOMATION = "REGISTER_SHAKE_AUTOMATION"
const val UNREGISTER_SHAKE_AUTOMATION = "UNREGISTER_SHAKE_AUTOMATION"

const val DEVICE_IDS = "DEVICE_IDS"
const val TAG = "${GLOBAL_TAG}#UDP_FOREGROUND_SERVICE"

/**
 * BroadcastReceiver that listens for Shake Automation registration requests
 *
 * You have to send an Intent with action REGISTER_SHAKE_AUTOMATION or UNREGISTER_SHAKE_AUTOMATION
 */
class ShakeAutomationRegistrationBroadcastReceiver :
    BroadcastReceiver() {
    private lateinit var sensorManager: SensorManager
    private lateinit var appRepository: AppRepository
    private var shakeEventListener: ShakeEventListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == null) {
            Log.d(TAG, "[onReceive]: No action received")
            return
        }
        Log.d(TAG, "[onReceive]: [$action] request received")
        val deviceIds = intent.getLongArrayExtra(DEVICE_IDS)

        when (action) {
            REGISTER_SHAKE_AUTOMATION -> {
                Log.d(
                    TAG,
                    "[onReceive]: Registering Shake Automation for devices: ${deviceIds?.contentToString()}"
                )
                registerShakeAutomation(deviceIds)
            }

            UNREGISTER_SHAKE_AUTOMATION -> {
                Log.d(
                    TAG,
                    "[onReceive]: Unregistering Shake Automation for devices: ${deviceIds?.contentToString()}"
                )
                unregisterShakeAutomation()
            }
        }


    }

    private fun registerShakeAutomation(deviceIds: LongArray?) {
        shakeEventListener = ShakeEventListener(callback = {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, "[registerShakeAutomation]: Shake detected")
                deviceIds?.let {
                    val devices = appRepository.getDevicesByIds(it.toList())
                    val isOn = devices.all { device -> device.isOn }
                    devices.forEach { device ->
                        UdpService.sendUdpMessage(
                            device.deviceId,
                            if (isOn) "toggle:off" else "toggle:on"
                        )
                        appRepository.updateDevice(device.copy(isOn = !isOn))
                    }
                }
            }
        })
        shakeEventListener?.let {
            registerShakeSensor(sensorManager, it)
        }
    }


    private fun unregisterShakeAutomation() {
        shakeEventListener?.let {
            unregisterShakeSensor(sensorManager, it)
            shakeEventListener = null
        }
    }

    fun setSensorManager(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
    }

    fun setAppRepository(appRepository: AppRepository) {
        this.appRepository = appRepository
    }
}

const val TAG_FOREGROUND_SERVICE = "${GLOBAL_TAG}#SHAKE_AUTOMATION_FOREGROUND_SERVICE"

@AndroidEntryPoint
class ShakeAutomationForegroundService : Service() {
    private lateinit var broadcastReceiver: ShakeAutomationRegistrationBroadcastReceiver

    @Inject
    lateinit var appRepository: AppRepository

    override fun onCreate() {
        Log.d(TAG_FOREGROUND_SERVICE, "[onCreate]: Shake Automation Foreground Service started")
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(REGISTER_SHAKE_AUTOMATION)
            addAction(UNREGISTER_SHAKE_AUTOMATION)
        }

        val sensorManager: SensorManager =
            application.applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
        broadcastReceiver = ShakeAutomationRegistrationBroadcastReceiver()
        broadcastReceiver.setSensorManager(sensorManager)
        broadcastReceiver.setAppRepository(appRepository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d(
                    TAG_FOREGROUND_SERVICE,
                    "[onCreate]: Registering broadcast receiver for versions >= TIRAMISU"
                )
                registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
            } else {
                Log.wtf(
                    TAG_FOREGROUND_SERVICE,
                    "[onCreate]: Can't register broadcast receiver for versions < TIRAMISU. Not implemented yet"
                )
            }
        } else {
            Log.wtf(
                TAG_FOREGROUND_SERVICE,
                "[onCreate]: Can't register broadcast receiver for versions < OREO. Not implemented yet"
            )
        }

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            TAG_FOREGROUND_SERVICE,
            "[onStartCommand]: Shake Automation Foreground Service started"
        )
        val notification = NotificationCompat.Builder(this, "SHAKE_SERVICE_CHANNEL")
            .setContentTitle("Home Assistant")
            .setContentText("Service running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_FOREGROUND_SERVICE, "[onDestroy]: Shake Automation Foreground Service stopped")
        unregisterReceiver(broadcastReceiver)
    }
}