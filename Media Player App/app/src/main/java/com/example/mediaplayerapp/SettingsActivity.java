package com.example.mediaplayerapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "media_player_prefs";
    public static final String KEY_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkModeEnabled = preferences.getBoolean(KEY_DARK_MODE, true);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchTheme = findViewById(R.id.switchTheme);
        switchTheme.setChecked(darkModeEnabled);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }
}
