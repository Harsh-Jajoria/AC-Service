package com.service.acservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.service.acservice.databinding.ActivityProfileBinding;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        setListener();
    }

    private void setListener() {
        binding.tvName.setText(preferenceManager.getString(Constants.USERNAME));
        binding.tvEmail.setText(preferenceManager.getString(Constants.EMAIL));
        binding.btnLogout.setOnClickListener(v -> {
            preferenceManager.clear();
            Intent intent = new Intent();
            intent.putExtra("finish", 1);
            setResult(12, intent);
            ProfileActivity.super.onBackPressed();
        });
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

}