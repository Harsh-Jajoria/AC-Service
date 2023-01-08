package com.service.acservice.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.acservice.databinding.ItemSelectedImageBinding;
import com.service.acservice.listener.SelectedImageListener;

import java.util.List;

public class AdapterImages extends RecyclerView.Adapter<AdapterImages.SelectedImageViewHolder> {

    List<String> selectedImagesList;
    SelectedImageListener selectedImageListener;

    public AdapterImages(List<String> selectedImagesList, SelectedImageListener selectedImageListener) {
        this.selectedImagesList = selectedImagesList;
        this.selectedImageListener = selectedImageListener;
    }

    @NonNull
    @Override
    public SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedImageViewHolder(ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        holder.setData(selectedImagesList.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedImagesList.size();
    }

    class SelectedImageViewHolder extends RecyclerView.ViewHolder {
        ItemSelectedImageBinding binding;

        SelectedImageViewHolder(ItemSelectedImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(String s) {
            binding.imgSelectedImage.setImageBitmap(base64ToBitmap(s));
            binding.imgSelectedImage.setOnClickListener(v -> selectedImageListener.onClicked(s));
            binding.cvDelete.setOnClickListener(v -> selectedImageListener.onDelete(s, getAdapterPosition()));
        }
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
