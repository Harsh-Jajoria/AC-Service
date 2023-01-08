package com.service.acservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.service.acservice.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding binding;
    private static final int TIMER = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.cvBack.setOnClickListener(v -> onBackPressed());
        binding.btnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnText.setVisibility(View.GONE);
                binding.buttonProgress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(this::resetButton, TIMER);
            }

            private void resetButton() {
                binding.buttonProgress.setVisibility(View.GONE);
                binding.btnText.setVisibility(View.VISIBLE);
                onBackPressed();
            }
        });
    }
}