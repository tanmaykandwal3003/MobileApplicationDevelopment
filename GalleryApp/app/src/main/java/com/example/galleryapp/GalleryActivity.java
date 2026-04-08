package com.example.galleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    public static final String EXTRA_FOLDER_URI = "extra_folder_uri";
    private static final String PREFS_NAME = "gallery_prefs";
    private static final String KEY_FOLDER_URI = "folder_uri";

    private TextView textFolderPath;
    private TextView textEmpty;
    private RecyclerView recyclerImages;
    private String currentFolderUri;

    private final ActivityResultLauncher<Uri> openFolderLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    return;
                }
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                currentFolderUri = uri.toString();
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString(KEY_FOLDER_URI, currentFolderUri).apply();
                loadFolderImages();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        textFolderPath = findViewById(R.id.textFolderPath);
        textEmpty = findViewById(R.id.textEmpty);
        recyclerImages = findViewById(R.id.recyclerImages);
        Button buttonChangeFolder = findViewById(R.id.buttonChangeFolder);
        recyclerImages.setLayoutManager(new GridLayoutManager(this, 3));
        buttonChangeFolder.setOnClickListener(v -> openFolderLauncher.launch(null));

        loadFolderImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolderImages();
    }

    private void loadFolderImages() {
        if (currentFolderUri == null || currentFolderUri.isEmpty()) {
            currentFolderUri = getIntent().getStringExtra(EXTRA_FOLDER_URI);
        }
        if (currentFolderUri == null || currentFolderUri.isEmpty()) {
            currentFolderUri = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_FOLDER_URI, null);
        }

        if (currentFolderUri == null || currentFolderUri.isEmpty()) {
            Toast.makeText(this, "Please choose a folder first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri treeUri = Uri.parse(currentFolderUri);
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            Toast.makeText(this, "Selected folder is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textFolderPath.setText(getString(R.string.folder_label, folder.getUri().toString()));

        List<ImageItem> imageItems = readImagesFromFolder(folder);
        ImageGridAdapter adapter = new ImageGridAdapter(imageItems, imageItem -> {
            Intent detailsIntent = new Intent(GalleryActivity.this, ImageDetailsActivity.class);
            detailsIntent.putExtra("uri", imageItem.getUri().toString());
            detailsIntent.putExtra("name", imageItem.getName());
            detailsIntent.putExtra("path", imageItem.getPath());
            detailsIntent.putExtra("sizeBytes", imageItem.getSizeBytes());
            detailsIntent.putExtra("dateTakenMillis", imageItem.getDateTakenMillis());
            startActivity(detailsIntent);
        });
        recyclerImages.setAdapter(adapter);

        textEmpty.setVisibility(imageItems.isEmpty() ? View.VISIBLE : View.GONE);
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
            // Fallback to last modified.
        }
        return file.lastModified();
    }
}
