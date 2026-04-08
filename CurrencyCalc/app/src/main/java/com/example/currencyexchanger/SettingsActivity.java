package com.example.currencyexchanger;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton backButton = findViewById(R.id.btnBack);
        Switch darkModeSwitch = findViewById(R.id.switchDarkMode);

        darkModeSwitch.setChecked(ThemeManager.getIsDarkMode(this));

        backButton.setOnClickListener(v -> finish());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeManager.setDarkMode(this, isChecked)
        );
    }
}
