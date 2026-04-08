package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
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
    private var isSensorRegistered = false

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
        registerSensorsIfNeeded()
    }

    override fun onPause() {
        unregisterSensorsIfNeeded()
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                updateAccelValues(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2]
                )
            }

            Sensor.TYPE_LIGHT -> {
                val newLightValue = event.values[0]
                if (lightValue != newLightValue) {
                    lightValue = newLightValue
                }
            }

            Sensor.TYPE_PROXIMITY -> {
                val newProximityValue = event.values[0]
                if (proximityValue != newProximityValue) {
                    proximityValue = newProximityValue
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    private fun registerSensorsIfNeeded() {
        if (isSensorRegistered) return

        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        isSensorRegistered = accelerometer != null || lightSensor != null || proximitySensor != null
    }

    private fun unregisterSensorsIfNeeded() {
        if (!isSensorRegistered) return
        sensorManager.unregisterListener(this)
        isSensorRegistered = false
    }

    private fun updateAccelValues(x: Float, y: Float, z: Float) {
        if (accelX != x) accelX = x
        if (accelY != y) accelY = y
        if (accelZ != z) accelZ = z
    }
}
