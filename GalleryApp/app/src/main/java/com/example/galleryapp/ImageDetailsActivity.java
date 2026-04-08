package com.example.galleryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ImageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        TextView textName = findViewById(R.id.textName);
        TextView textPath = findViewById(R.id.textPath);
        TextView textSize = findViewById(R.id.textSize);
        TextView textDateTaken = findViewById(R.id.textDateTaken);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        String name = getIntent().getStringExtra("name");
        String path = getIntent().getStringExtra("path");
        String size = getIntent().getStringExtra("size");
        String dateTaken = getIntent().getStringExtra("dateTaken");

        textName.setText(getString(R.string.label_name, safeValue(name)));
        textPath.setText(getString(R.string.label_path, safeValue(path)));
        textSize.setText(getString(R.string.label_size, safeValue(size)));
        textDateTaken.setText(getString(R.string.label_date_taken, safeValue(dateTaken)));

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
                    Toast.makeText(this, "Delete UI confirmed (logic in next step)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
