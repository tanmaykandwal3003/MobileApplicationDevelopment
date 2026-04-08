package com.example.galleryapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ExifInterfaceUtils {
    private ExifInterfaceUtils() {
    }

    public static long parseExifDate(String exifDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
            Date date = format.parse(exifDate);
            return date == null ? 0L : date.getTime();
        } catch (Exception ignored) {
            return 0L;
        }
    }
}
