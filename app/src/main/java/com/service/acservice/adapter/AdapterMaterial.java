package com.service.acservice.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.acservice.databinding.ItemMaterialBinding;
import com.service.acservice.listener.MaterialListener;
import com.service.acservice.model.request.Material;

import java.util.List;

public class AdapterMaterial extends RecyclerView.Adapter<AdapterMaterial.MaterialViewHolder> {
    List<Material> materialList;
    MaterialListener materialListener;

    public AdapterMaterial(List<Material> materialList, MaterialListener materialListener) {
        this.materialList = materialList;
        this.materialListener = materialListener;
    }

    public int sum(List<Material> materialList) {
        int sum = 0;
        for (int i = 0; i < materialList.size(); i++) {
            int a = Integer.parseInt(materialList.get(i).getTotal());
            sum = sum + a;
        }
        return sum;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MaterialViewHolder(
                ItemMaterialBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        holder.setData(materialList.get(position));
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        ItemMaterialBinding binding;

        MaterialViewHolder(ItemMaterialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Material material) {
            binding.tvMaterial.setText(material.getMaterial());
            binding.tvRate.setText(String.format("₹ %s/-", material.getRate()));
            binding.tvQty.setText(material.getQuantity());
            binding.tvTotal.setText(String.format("₹ %s/-", material.getTotal()));
            binding.imgRemoveMaterial.setOnClickListener(v -> {
                materialListener.onDeleteClicked(material, getAdapterPosition());
            });
        }
    }

}
