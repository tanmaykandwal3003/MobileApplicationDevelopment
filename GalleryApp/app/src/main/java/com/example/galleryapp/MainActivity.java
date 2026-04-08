package com.example.galleryapp;

import android.Manifest;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "gallery_prefs";
    private static final String KEY_FOLDER_URI = "folder_uri";

    private boolean pendingTakePhoto = false;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCameraPreview();
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Void> takePicturePreviewLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap == null) {
                    Toast.makeText(this, "No photo captured", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePhotoToSelectedFolder(bitmap);
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

        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonOpenFolder = findViewById(R.id.buttonOpenFolder);

        buttonTakePhoto.setOnClickListener(v -> onTakePhotoClicked());
        buttonOpenFolder.setOnClickListener(v -> onOpenFolderClicked());
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
            openCameraPreview();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCameraPreview() {
        takePicturePreviewLauncher.launch(null);
    }

    private void savePhotoToSelectedFolder(Bitmap bitmap) {
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

        try (OutputStream stream = getContentResolver().openOutputStream(imageFile.getUri())) {
            if (stream == null) {
                Toast.makeText(this, "Unable to save photo", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
            if (!saved) {
                Toast.makeText(this, "Could not save captured photo", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Photo saved to selected folder", Toast.LENGTH_SHORT).show();
            openGallery(folderUriText);
        } catch (Exception e) {
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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