package com.example.galleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Uri photoUri;

    // Launcher: take photo with camera
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Photo capture cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher: request permissions
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Boolean granted : result.values()) {
                    if (!granted) { allGranted = false; break; }
                }
                if (allGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTakePhoto     = findViewById(R.id.btnTakePhoto);
        Button btnBrowseGallery = findViewById(R.id.btnBrowseGallery);

        btnTakePhoto.setOnClickListener(v -> checkPermissionsAndLaunchCamera());
        btnBrowseGallery.setOnClickListener(v -> {
            startActivity(new Intent(this, GalleryActivity.class));
        });
    }

    private void checkPermissionsAndLaunchCamera() {
        List<String> needed = new ArrayList<>();
        needed.add(Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            needed.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        boolean allGranted = true;
        for (String perm : needed) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            launchCamera();
        } else {
            permissionLauncher.launch(needed.toArray(new String[0]));
        }
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Could not create image file: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Save to app's external Pictures folder
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String fileName = "IMG_" + timestamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }
}
