package com.example.galleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class ImagePreviewActivity extends AppCompatActivity {
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        ImageView imagePreview = findViewById(R.id.imagePreviewFull);
        Button buttonDetails = findViewById(R.id.buttonDetails);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        String uriText = getIntent().getStringExtra("uri");
        if (uriText != null && !uriText.isEmpty()) {
            imageUri = Uri.parse(uriText);
            imagePreview.setImageURI(imageUri);
        }

        buttonDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageDetailsActivity.class);
            intent.putExtra("uri", getIntent().getStringExtra("uri"));
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("path", getIntent().getStringExtra("path"));
            intent.putExtra("sizeBytes", getIntent().getLongExtra("sizeBytes", 0L));
            intent.putExtra("dateTakenMillis", getIntent().getLongExtra("dateTakenMillis", 0L));
            startActivity(intent);
        });

        buttonDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteImageAndGoBack())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteImageAndGoBack() {
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
}
