package com.example.galleryapp;

import android.Manifest;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "gallery_prefs";
    private static final String KEY_FOLDER_URI = "folder_uri";

    private boolean pendingTakePhoto = false;
    private Uri pendingCapturedImageUri;
    private RecyclerView recyclerHomeImages;
    private TextView textSelectedFolder;
    private TextView textHomeEmpty;

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
                    loadHomeImages();
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
                    loadHomeImages();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonOpenFolder = findViewById(R.id.buttonOpenFolder);
        textSelectedFolder = findViewById(R.id.textSelectedFolder);
        textHomeEmpty = findViewById(R.id.textHomeEmpty);
        recyclerHomeImages = findViewById(R.id.recyclerHomeImages);
        recyclerHomeImages.setLayoutManager(new GridLayoutManager(this, 3));

        buttonTakePhoto.setOnClickListener(v -> onTakePhotoClicked());
        buttonOpenFolder.setOnClickListener(v -> onOpenFolderClicked());
        loadHomeImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHomeImages();
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

    private void loadHomeImages() {
        String folderUriText = getSavedFolderUri();
        if (folderUriText == null || folderUriText.isEmpty()) {
            textSelectedFolder.setVisibility(View.GONE);
            recyclerHomeImages.setAdapter(new ImageGridAdapter(Collections.emptyList(), imageItem -> {}));
            textHomeEmpty.setVisibility(View.VISIBLE);
            return;
        }

        Uri treeUri = Uri.parse(folderUriText);
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            textSelectedFolder.setVisibility(View.GONE);
            recyclerHomeImages.setAdapter(new ImageGridAdapter(Collections.emptyList(), imageItem -> {}));
            textHomeEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textSelectedFolder.setVisibility(View.GONE);
        List<ImageItem> images = readImagesFromFolder(folder);
        ImageGridAdapter adapter = new ImageGridAdapter(images, imageItem -> {
            Intent previewIntent = new Intent(MainActivity.this, ImagePreviewActivity.class);
            previewIntent.putExtra("uri", imageItem.getUri().toString());
            previewIntent.putExtra("name", imageItem.getName());
            previewIntent.putExtra("path", imageItem.getPath());
            previewIntent.putExtra("sizeBytes", imageItem.getSizeBytes());
            previewIntent.putExtra("dateTakenMillis", imageItem.getDateTakenMillis());
            startActivity(previewIntent);
        });
        recyclerHomeImages.setAdapter(adapter);
        textHomeEmpty.setVisibility(images.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private List<ImageItem> readImagesFromFolder(DocumentFile folder) {
        DocumentFile[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        Arrays.sort(files, Comparator.comparingLong(DocumentFile::lastModified).reversed());
        List<ImageItem> images = new ArrayList<>();
        for (DocumentFile file : files) {
            if (!file.isFile()) {
                continue;
            }
            String type = file.getType();
            if (type == null || !type.toLowerCase(Locale.US).startsWith("image/")) {
                continue;
            }
            images.add(new ImageItem(
                    file.getName() == null ? "Unknown" : file.getName(),
                    file.getUri(),
                    file.getUri().toString(),
                    file.length(),
                    resolveDateTaken(file)
            ));
        }
        return images;
    }

    private long resolveDateTaken(DocumentFile file) {
        try (InputStream stream = getContentResolver().openInputStream(file.getUri())) {
            if (stream != null) {
                ExifInterface exif = new ExifInterface(stream);
                String dateTimeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
                if (dateTimeOriginal != null) {
                    long exifTime = ExifInterfaceUtils.parseExifDate(dateTimeOriginal);
                    if (exifTime > 0L) {
                        return exifTime;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return file.lastModified();
    }

    private String getSavedFolderUri() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_FOLDER_URI, null);
    }

    private void saveSelectedFolderUri(String uri) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_FOLDER_URI, uri).apply();
    }
}