package com.example.sensors;

import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView accelerometerValueX;
    private TextView accelerometerValueY;
    private TextView accelerometerValueZ;
    private TextView lightValue;
    private TextView proximityValue;
    private TextView availabilityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        accelerometerValueX = findViewById(R.id.accelerometerValueX);
        accelerometerValueY = findViewById(R.id.accelerometerValueY);
        accelerometerValueZ = findViewById(R.id.accelerometerValueZ);
        lightValue = findViewById(R.id.lightValue);
        proximityValue = findViewById(R.id.proximityValue);
        availabilityValue = findViewById(R.id.availabilityValue);

        availabilityValue.setText(buildAvailabilityText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorIfAvailable(accelerometerSensor);
        registerSensorIfAvailable(lightSensor);
        registerSensorIfAvailable(proximitySensor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValueX.setText(getString(R.string.value_with_unit_ms2, formatDecimal(event.values[0])));
            accelerometerValueY.setText(getString(R.string.value_with_unit_ms2, formatDecimal(event.values[1])));
            accelerometerValueZ.setText(getString(R.string.value_with_unit_ms2, formatDecimal(event.values[2])));
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightValue.setText(getString(R.string.value_with_unit_lux, formatDecimal(event.values[0])));
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityValue.setText(getString(R.string.value_with_unit_cm, formatDecimal(event.values[0])));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed for this app.
    }

    private void registerSensorIfAvailable(Sensor sensor) {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private String buildAvailabilityText() {
        String accelerometerStatus = accelerometerSensor == null
                ? getString(R.string.sensor_missing)
                : getString(R.string.sensor_available);
        String lightStatus = lightSensor == null
                ? getString(R.string.sensor_missing)
                : getString(R.string.sensor_available);
        String proximityStatus = proximitySensor == null
                ? getString(R.string.sensor_missing)
                : getString(R.string.sensor_available);

        return getString(
                R.string.sensor_availability_format,
                accelerometerStatus,
                lightStatus,
                proximityStatus
        );
    }

    private String formatDecimal(float value) {
        return String.format(Locale.US, "%.2f", value);
    }
}