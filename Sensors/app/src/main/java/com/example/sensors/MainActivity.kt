package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sensors.ui.SensorMonitorScreen

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null

    private var accelX by mutableStateOf(0f)
    private var accelY by mutableStateOf(0f)
    private var accelZ by mutableStateOf(0f)
    private var lightValue by mutableStateOf(0f)
    private var proximityValue by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (accelerometer == null) {
            Log.w(TAG, "Sensor not available: Accelerometer")
        }
        if (lightSensor == null) {
            Log.w(TAG, "Sensor not available: Light")
        }
        if (proximitySensor == null) {
            Log.w(TAG, "Sensor not available: Proximity")
        }

        enableEdgeToEdge()
        setContent {
            SensorMonitorScreen(
                accelX = accelX,
                accelY = accelY,
                accelZ = accelZ,
                light = lightValue,
                proximity = proximityValue,
                accelerometerAvailable = accelerometer != null,
                lightAvailable = lightSensor != null,
                proximityAvailable = proximitySensor != null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelX = event.values[0]
                accelY = event.values[1]
                accelZ = event.values[2]
                Log.d(TAG, "Accel: X=$accelX Y=$accelY Z=$accelZ")
            }

            Sensor.TYPE_LIGHT -> {
                lightValue = event.values[0]
                Log.d(TAG, "Light: $lightValue")
            }

            Sensor.TYPE_PROXIMITY -> {
                proximityValue = event.values[0]
                Log.d(TAG, "Proximity: $proximityValue")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "Accuracy changed for ${sensor.name}: $accuracy")
    }

    companion object {
        private const val TAG = "SENSOR"
    }
}
