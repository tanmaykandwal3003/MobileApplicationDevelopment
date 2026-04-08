package com.example.galleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    public static final String EXTRA_FOLDER_URI = "extra_folder_uri";

    private TextView textFolderPath;
    private TextView textEmpty;
    private RecyclerView recyclerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        textFolderPath = findViewById(R.id.textFolderPath);
        textEmpty = findViewById(R.id.textEmpty);
        recyclerImages = findViewById(R.id.recyclerImages);
        recyclerImages.setLayoutManager(new GridLayoutManager(this, 3));

        loadFolderImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolderImages();
    }

    private void loadFolderImages() {
        String folderUriString = getIntent().getStringExtra(EXTRA_FOLDER_URI);
        if (folderUriString == null || folderUriString.isEmpty()) {
            folderUriString = getSharedPreferences("gallery_prefs", MODE_PRIVATE).getString("folder_uri", null);
        }

        if (folderUriString == null || folderUriString.isEmpty()) {
            Toast.makeText(this, "Please choose a folder first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri treeUri = Uri.parse(folderUriString);
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
                    file.lastModified()
            ));
        }
        return images;
    }
}
