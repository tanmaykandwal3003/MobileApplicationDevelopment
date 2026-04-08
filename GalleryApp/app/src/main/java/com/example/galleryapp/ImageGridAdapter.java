package com.example.galleryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder> {

    public interface OnImageClickListener {
        void onImageClicked(ImageItem imageItem);
    }

    private final List<ImageItem> imageItems;
    private final OnImageClickListener listener;

    public ImageGridAdapter(List<ImageItem> imageItems, OnImageClickListener listener) {
        this.imageItems = imageItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem currentItem = imageItems.get(position);
        holder.imageThumb.setImageURI(currentItem.getUri());

        holder.itemView.setOnClickListener(v -> listener.onImageClicked(currentItem));
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumb;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumb = itemView.findViewById(R.id.imageThumb);
        }
    }
}
