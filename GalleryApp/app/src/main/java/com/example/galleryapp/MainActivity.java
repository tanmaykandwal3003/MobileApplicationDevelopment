package com.example.galleryapp;

import android.Manifest;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "gallery_prefs";
    private static final String KEY_FOLDER_URI = "folder_uri";

    private boolean pendingTakePhoto = false;
    private Uri pendingCapturedImageUri;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCameraCapture();
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), isSaved -> {
                if (isSaved) {
                    pendingCapturedImageUri = null;
                    Toast.makeText(this, "Photo saved to selected folder", Toast.LENGTH_SHORT).show();
                    String folderUriText = getSavedFolderUri();
                    if (folderUriText != null) {
                        openGallery(folderUriText);
                    }
                } else {
                    cleanupFailedCapture();
                    Toast.makeText(this, "No photo captured", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> openFolderLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    pendingTakePhoto = false;
                    Toast.makeText(this, "Folder selection canceled", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                saveSelectedFolderUri(uri.toString());

                if (pendingTakePhoto) {
                    pendingTakePhoto = false;
                    ensureCameraPermissionAndCapture();
                } else {
                    openGallery(uri.toString());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View cardTakePhoto = findViewById(R.id.cardTakePhoto);
        View cardOpenFolder = findViewById(R.id.cardOpenFolder);

        cardTakePhoto.setOnClickListener(v -> onTakePhotoClicked());
        cardOpenFolder.setOnClickListener(v -> onOpenFolderClicked());
    }

    private void onTakePhotoClicked() {
        String savedFolderUri = getSavedFolderUri();
        if (savedFolderUri == null) {
            pendingTakePhoto = true;
            openFolderLauncher.launch(null);
            return;
        }
        ensureCameraPermissionAndCapture();
    }

    private void onOpenFolderClicked() {
        pendingTakePhoto = false;
        openFolderLauncher.launch(null);
    }

    private void ensureCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCameraCapture();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCameraCapture() {
        String folderUriText = getSavedFolderUri();
        if (folderUriText == null) {
            Toast.makeText(this, "Please choose a folder first", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri folderUri = Uri.parse(folderUriText);
        DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            Toast.makeText(this, "Selected folder is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg";
        DocumentFile imageFile = folder.createFile("image/jpeg", fileName);
        if (imageFile == null) {
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
            return;
        }
        pendingCapturedImageUri = imageFile.getUri();
        takePictureLauncher.launch(pendingCapturedImageUri);
    }

    private void cleanupFailedCapture() {
        if (pendingCapturedImageUri == null) {
            return;
        }
        DocumentFile file = DocumentFile.fromSingleUri(this, pendingCapturedImageUri);
        if (file != null && file.exists()) {
            file.delete();
        }
        pendingCapturedImageUri = null;
    }

    private void openGallery(String folderUri) {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXTRA_FOLDER_URI, folderUri);
        startActivity(intent);
    }

    private String getSavedFolderUri() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_FOLDER_URI, null);
    }

    private void saveSelectedFolderUri(String uri) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_FOLDER_URI, uri).apply();
    }
}