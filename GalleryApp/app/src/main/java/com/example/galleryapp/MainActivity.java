package com.example.galleryapp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonOpenFolder = findViewById(R.id.buttonOpenFolder);

        buttonTakePhoto.setOnClickListener(v ->
                Toast.makeText(this, "Camera UI is ready (functionality in next step)", Toast.LENGTH_SHORT).show()
        );

        buttonOpenFolder.setOnClickListener(v ->
                startActivity(new Intent(this, GalleryActivity.class))
        );
    }
}