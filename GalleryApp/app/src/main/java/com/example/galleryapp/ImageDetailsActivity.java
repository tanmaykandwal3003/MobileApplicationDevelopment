package com.example.galleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import java.io.InputStream;
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
        TextView textCamera = findViewById(R.id.textCamera);
        TextView textLens = findViewById(R.id.textLens);
        TextView textLocation = findViewById(R.id.textLocation);
        TextView textExifExtras = findViewById(R.id.textExifExtras);

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
        fillExifMetadata(textCamera, textLens, textLocation, textExifExtras);

    }

    private String safeValue(String value) {
        return value == null || value.isEmpty() ? "N/A" : value;
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

    private void fillExifMetadata(TextView textCamera, TextView textLens, TextView textLocation, TextView textExifExtras) {
        if (imageUri == null) {
            textCamera.setText(getString(R.string.label_camera, "N/A"));
            textLens.setText(getString(R.string.label_lens, "N/A"));
            textLocation.setText(getString(R.string.label_location, "N/A"));
            textExifExtras.setText(getString(R.string.label_exif_extras, "N/A"));
            return;
        }

        try (InputStream stream = getContentResolver().openInputStream(imageUri)) {
            if (stream == null) {
                throw new IllegalStateException();
            }

            ExifInterface exif = new ExifInterface(stream);
            String camera = joinValues(exif.getAttribute(ExifInterface.TAG_MAKE), exif.getAttribute(ExifInterface.TAG_MODEL));
            String lens = safeExif(exif.getAttribute(ExifInterface.TAG_LENS_MODEL));

            float[] latLong = new float[2];
            String location = "N/A";
            if (exif.getLatLong(latLong)) {
                location = String.format(Locale.getDefault(), "%.6f, %.6f", latLong[0], latLong[1]);
            }

            String iso = safeExif(exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY));
            String focal = safeExif(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
            String exposure = safeExif(exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME));
            String extras = "ISO: " + iso + ", Focal Length: " + focal + ", Exposure: " + exposure;

            textCamera.setText(getString(R.string.label_camera, camera));
            textLens.setText(getString(R.string.label_lens, lens));
            textLocation.setText(getString(R.string.label_location, location));
            textExifExtras.setText(getString(R.string.label_exif_extras, extras));
        } catch (Exception e) {
            textCamera.setText(getString(R.string.label_camera, "N/A"));
            textLens.setText(getString(R.string.label_lens, "N/A"));
            textLocation.setText(getString(R.string.label_location, "N/A"));
            textExifExtras.setText(getString(R.string.label_exif_extras, "N/A"));
        }
    }

    private String safeExif(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value;
    }

    private String joinValues(String first, String second) {
        String f = safeExif(first);
        String s = safeExif(second);
        if ("N/A".equals(f) && "N/A".equals(s)) {
            return "N/A";
        }
        if ("N/A".equals(f)) {
            return s;
        }
        if ("N/A".equals(s)) {
            return f;
        }
        return f + " " + s;
    }
}
