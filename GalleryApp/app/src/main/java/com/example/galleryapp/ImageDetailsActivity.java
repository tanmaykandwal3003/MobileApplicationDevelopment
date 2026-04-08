package com.example.galleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imagePreview = findViewById(R.id.imagePreview);
        TextView textName = findViewById(R.id.textName);
        TextView textPath = findViewById(R.id.textPath);
        TextView textSize = findViewById(R.id.textSize);
        TextView textDateTaken = findViewById(R.id.textDateTaken);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        String uriText = getIntent().getStringExtra("uri");
        String name = getIntent().getStringExtra("name");
        String path = getIntent().getStringExtra("path");
        long sizeBytes = getIntent().getLongExtra("sizeBytes", 0L);
        long dateTakenMillis = getIntent().getLongExtra("dateTakenMillis", 0L);

        if (uriText != null && !uriText.isEmpty()) {
            imageUri = Uri.parse(uriText);
            imagePreview.setImageURI(imageUri);
        }

        textName.setText(getString(R.string.label_name, safeValue(name)));
        textPath.setText(getString(R.string.label_path, safeValue(path)));
        textSize.setText(getString(R.string.label_size, formatSize(sizeBytes)));
        textDateTaken.setText(getString(R.string.label_date_taken, formatDate(dateTakenMillis)));

        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private String safeValue(String value) {
        return value == null || value.isEmpty() ? "N/A" : value;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteImageAndClose();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteImageAndClose() {
        if (imageUri == null) {
            Toast.makeText(this, "Unable to delete image", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentFile file = DocumentFile.fromSingleUri(this, imageUri);
        if (file != null && file.exists() && file.delete()) {
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(long dateTakenMillis) {
        if (dateTakenMillis <= 0L) {
            return "N/A";
        }
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
                .format(new Date(dateTakenMillis));
    }

    private String formatSize(long sizeBytes) {
        if (sizeBytes <= 0L) {
            return "0 B";
        }
        if (sizeBytes < 1024L) {
            return sizeBytes + " B";
        }
        double kb = sizeBytes / 1024.0;
        if (kb < 1024.0) {
            return String.format(Locale.getDefault(), "%.1f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024.0) {
            return String.format(Locale.getDefault(), "%.2f MB", mb);
        }
        double gb = mb / 1024.0;
        return String.format(Locale.getDefault(), "%.2f GB", gb);
    }
}
