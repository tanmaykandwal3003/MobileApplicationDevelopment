package com.example.galleryapp;

import android.net.Uri;

public class ImageItem {
    private final String name;
    private final Uri uri;
    private final String path;
    private final long sizeBytes;
    private final long dateTakenMillis;

    public ImageItem(String name, Uri uri, String path, long sizeBytes, long dateTakenMillis) {
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.dateTakenMillis = dateTakenMillis;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public long getDateTakenMillis() {
        return dateTakenMillis;
    }
}
