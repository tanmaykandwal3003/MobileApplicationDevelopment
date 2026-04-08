package com.example.galleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private TextView tvFolderPath, tvImageCount, tvEmpty;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<File> imageFiles = new ArrayList<>();
    private String currentFolderPath = null;

    // Launcher: pick folder via document tree
    private final ActivityResultLauncher<Uri> folderPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri != null) {
                    // Convert content URI to real path via Environment
                    // Fallback: use DCIM or Pictures as default and let user navigate
                    String path = getRealPathFromUri(uri);
                    if (path != null) {
                        loadImagesFromFolder(path);
                    } else {
                        Toast.makeText(this, "Could not resolve folder path.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // Launcher: storage permissions
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = true;
                for (Boolean g : result.values()) { if (!g) { granted = false; break; } }
                if (granted) openFolderPicker();
                else Toast.makeText(this, "Storage permission required.", Toast.LENGTH_LONG).show();
            });

    // Launcher: return from ImageDetailActivity (image may have been deleted)
    private final ActivityResultLauncher<Intent> detailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    (ActivityResult result) -> {
                        // Refresh gallery after returning from detail
                        if (currentFolderPath != null) {
                            loadImagesFromFolder(currentFolderPath);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        tvFolderPath  = findViewById(R.id.tvFolderPath);
        tvImageCount  = findViewById(R.id.tvImageCount);
        tvEmpty       = findViewById(R.id.tvEmpty);
        recyclerView  = findViewById(R.id.recyclerView);
        Button btnChooseFolder = findViewById(R.id.btnChooseFolder);

        // 3-column grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(imageFiles, file -> {
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_PATH, file.getAbsolutePath());
            detailLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);

        btnChooseFolder.setOnClickListener(v -> checkPermissionsAndPickFolder());

        // Default: load from app's Pictures folder
        File defaultDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (defaultDir != null && defaultDir.exists()) {
            loadImagesFromFolder(defaultDir.getAbsolutePath());
        }
    }

    private void checkPermissionsAndPickFolder() {
        List<String> needed = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            needed.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            needed.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        boolean allGranted = true;
        for (String p : needed) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false; break;
            }
        }
        if (allGranted) openFolderPicker();
        else permissionLauncher.launch(needed.toArray(new String[0]));
    }

    private void openFolderPicker() {
        folderPickerLauncher.launch(null);
    }

    /**
     * Attempts to get a real filesystem path from a content:// tree URI.
     * Works for primary external storage paths like /storage/emulated/0/...
     */
    private String getRealPathFromUri(Uri uri) {
        String uriStr = uri.toString();
        // content://com.android.externalstorage.documents/tree/primary%3ADCIM
        if (uriStr.contains("primary%3A")) {
            String relativePath = uriStr.substring(uriStr.indexOf("primary%3A") + "primary%3A".length());
            relativePath = Uri.decode(relativePath);
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + relativePath;
        }
        // Fallback: use DCIM
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    void loadImagesFromFolder(String folderPath) {
        currentFolderPath = folderPath;
        File folder = new File(folderPath);
        imageFiles.clear();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (isImageFile(f)) {
                        imageFiles.add(f);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();

        tvFolderPath.setText(folderPath);
        tvImageCount.setText(imageFiles.size() + " image(s)");

        if (imageFiles.isEmpty()) {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            tvEmpty.setText("No images found in this folder.");
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    private boolean isImageFile(File file) {
        if (!file.isFile()) return false;
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".png") || name.endsWith(".gif")
                || name.endsWith(".bmp") || name.endsWith(".webp");
    }
}
