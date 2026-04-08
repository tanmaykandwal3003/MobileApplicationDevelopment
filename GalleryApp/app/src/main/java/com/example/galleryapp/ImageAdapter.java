package com.example.galleryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(File file);
    }

    private final List<File> images;
    private final OnImageClickListener listener;

    public ImageAdapter(List<File> images, OnImageClickListener listener) {
        this.images   = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = images.get(position);
        Glide.with(holder.ivThumbnail.getContext())
                .load(file)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onImageClick(file));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
