package com.example.galleryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "image_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (imagePath == null) {
            finish();
            return;
        }

        File imageFile = new File(imagePath);

        ImageView ivFullImage = findViewById(R.id.ivFullImage);
        TextView tvName       = findViewById(R.id.tvName);
        TextView tvPath       = findViewById(R.id.tvPath);
        TextView tvSize       = findViewById(R.id.tvSize);
        TextView tvDate       = findViewById(R.id.tvDate);
        Button btnDelete      = findViewById(R.id.btnDelete);

        // Load full image
        Glide.with(this).load(imageFile).into(ivFullImage);

        // Populate details
        tvName.setText(imageFile.getName());
        tvPath.setText(imageFile.getAbsolutePath());
        tvSize.setText(formatFileSize(imageFile.length()));
        tvDate.setText(formatDate(imageFile.lastModified()));

        // Delete with confirmation dialog
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete \"" + imageFile.getName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (imageFile.delete()) {
                            Toast.makeText(this, "Image deleted.", Toast.LENGTH_SHORT).show();
                            // Return to gallery (result signals refresh)
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        DecimalFormat df = new DecimalFormat("#.##");
        if (bytes < 1024 * 1024) return df.format(bytes / 1024.0) + " KB";
        return df.format(bytes / (1024.0 * 1024)) + " MB";
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
