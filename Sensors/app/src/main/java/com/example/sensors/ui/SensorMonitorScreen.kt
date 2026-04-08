package com.example.sensors.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorMonitorScreen(
    accelX: Float,
    accelY: Float,
    accelZ: Float,
    light: Float,
    proximity: Float,
    accelerometerAvailable: Boolean = true,
    lightAvailable: Boolean = true,
    proximityAvailable: Boolean = true
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Sensor Monitor") }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AccelerometerCard(
                    accelX = accelX,
                    accelY = accelY,
                    accelZ = accelZ,
                    isAvailable = accelerometerAvailable
                )
                LightSensorCard(
                    light = light,
                    isAvailable = lightAvailable
                )
                ProximitySensorCard(
                    proximity = proximity,
                    isAvailable = proximityAvailable
                )
            }
        }
    }
}

@Composable
private fun SensorReadingCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun AccelerometerCard(
    accelX: Float,
    accelY: Float,
    accelZ: Float,
    isAvailable: Boolean
) {
    SensorReadingCard {
        Text(
            text = "Accelerometer",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (!isAvailable) {
            ValueText(valueText = "Not Available")
        } else {
            ValueText(valueText = "X: ${String.format(\"%.2f\", accelX)}")
            ValueText(valueText = "Y: ${String.format(\"%.2f\", accelY)}")
            ValueText(valueText = "Z: ${String.format(\"%.2f\", accelZ)}")
        }
    }
}

@Composable
private fun LightSensorCard(
    light: Float,
    isAvailable: Boolean
) {
    SensorReadingCard {
        Text(
            text = "Light Sensor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (!isAvailable) {
            ValueText(valueText = "Not Available")
        } else {
            ValueText(valueText = "Light Level: ${String.format(\"%.2f\", light)} lx")
        }
    }
}

@Composable
private fun ProximitySensorCard(
    proximity: Float,
    isAvailable: Boolean
) {
    SensorReadingCard {
        Text(
            text = "Proximity Sensor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (!isAvailable) {
            ValueText(valueText = "Not Available")
        } else {
            ValueText(valueText = "Distance: ${String.format(\"%.2f\", proximity)} cm")
        }
    }
}

@Composable
private fun ValueText(valueText: String) {
    Text(
        text = valueText,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
private fun SensorMonitorScreenPreview() {
    SensorMonitorScreen(
        accelX = 1.23f,
        accelY = 4.56f,
        accelZ = 7.89f,
        light = 120.0f,
        proximity = 5.0f
    )
}
