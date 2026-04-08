package com.example.sensorsfinal;

import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView accelerometerXText;
    private TextView accelerometerYText;
    private TextView accelerometerZText;
    private TextView lightValueText;
    private TextView proximityValueText;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSensors();
        updateAvailabilityStatus();
    }

    private void initViews() {
        accelerometerXText = findViewById(R.id.text_accelerometer_x);
        accelerometerYText = findViewById(R.id.text_accelerometer_y);
        accelerometerZText = findViewById(R.id.text_accelerometer_z);
        lightValueText = findViewById(R.id.text_light_value);
        proximityValueText = findViewById(R.id.text_proximity_value);
        statusText = findViewById(R.id.text_status);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            return;
        }

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void updateAvailabilityStatus() {
        if (sensorManager == null) {
            statusText.setText(R.string.status_sensor_manager_unavailable);
            showUnavailableValues();
            return;
        }

        StringBuilder availability = new StringBuilder(getString(R.string.status_available_prefix));
        availability.append("\n");
        availability.append(getString(R.string.label_accelerometer)).append(": ")
                .append(sensorLabel(accelerometerSensor != null)).append("\n");
        availability.append(getString(R.string.label_light)).append(": ")
                .append(sensorLabel(lightSensor != null)).append("\n");
        availability.append(getString(R.string.label_proximity)).append(": ")
                .append(sensorLabel(proximitySensor != null));

        statusText.setText(availability.toString());
        showUnavailableValues();
    }

    private String sensorLabel(boolean available) {
        return available ? getString(R.string.available) : getString(R.string.not_available);
    }

    private void showUnavailableValues() {
        if (accelerometerSensor == null) {
            String na = getString(R.string.not_available_short);
            accelerometerXText.setText(getString(R.string.acc_x, na));
            accelerometerYText.setText(getString(R.string.acc_y, na));
            accelerometerZText.setText(getString(R.string.acc_z, na));
        }

        if (lightSensor == null) {
            String na = getString(R.string.light_value, getString(R.string.not_available_short));
            lightValueText.setText(na);
        }

        if (proximitySensor == null) {
            String na = getString(R.string.proximity_value, getString(R.string.not_available_short));
            proximityValueText.setText(na);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensor(accelerometerSensor);
        registerSensor(lightSensor);
        registerSensor(proximitySensor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void registerSensor(Sensor sensor) {
        if (sensorManager != null && sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accelerometerXText.setText(getString(R.string.acc_x, formatValue(event.values[0])));
            accelerometerYText.setText(getString(R.string.acc_y, formatValue(event.values[1])));
            accelerometerZText.setText(getString(R.string.acc_z, formatValue(event.values[2])));
        } else if (sensorType == Sensor.TYPE_LIGHT) {
            lightValueText.setText(getString(R.string.light_value, formatValue(event.values[0])));
        } else if (sensorType == Sensor.TYPE_PROXIMITY) {
            proximityValueText.setText(getString(R.string.proximity_value, formatValue(event.values[0])));
        }
    }

    private String formatValue(float value) {
        return String.format(Locale.US, "%.2f", value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Accuracy updates are not required for this UI.
    }
}