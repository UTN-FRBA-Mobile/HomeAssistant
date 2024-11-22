package ar.edu.utn.frba.homeassistant.utils

import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// Base: https://www.geeksforgeeks.org/how-to-detect-shake-event-in-android/
class ShakeEventListener(
    private val callback: () -> Unit,
    private val threshold: Float = 12f
) : SensorEventListener {

    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var acceleration = 10f

    init {
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        lastAcceleration = currentAcceleration

        currentAcceleration = sqrt(x * x + y * y + z * z.toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta
        if (currentAcceleration > threshold) {
            callback()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something
    }
}

fun registerShakeSensor(sensorManager: SensorManager, callback: () -> Unit){
    val sensorShake = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
    sensorManager.registerListener(ShakeEventListener(callback = callback), sensorShake, SensorManager.SENSOR_DELAY_NORMAL)
}

fun unregisterShakeSensor(sensorManager: SensorManager, callback: () -> Unit){
    val sensorShake = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
    sensorManager.unregisterListener(ShakeEventListener(callback = callback), sensorShake)
}

