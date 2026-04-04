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
fun SensorMonitorScreen() {
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
                AccelerometerCard()
                LightSensorCard()
                ProximitySensorCard()
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
private fun AccelerometerCard() {
    SensorReadingCard {
        Text(
            text = "Accelerometer",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "X: 0.0")
        Text(text = "Y: 0.0")
        Text(text = "Z: 0.0")
    }
}

@Composable
private fun LightSensorCard() {
    SensorReadingCard {
        Text(
            text = "Light Sensor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Light Level: 0.0 lx")
    }
}

@Composable
private fun ProximitySensorCard() {
    SensorReadingCard {
        Text(
            text = "Proximity Sensor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Distance: 0.0 cm")
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorMonitorScreenPreview() {
    SensorMonitorScreen()
}
