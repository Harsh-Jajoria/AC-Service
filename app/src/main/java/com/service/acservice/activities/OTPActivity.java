package com.service.acservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.service.acservice.databinding.ActivityOtpactivityBinding;

public class OTPActivity extends AppCompatActivity {
    ActivityOtpactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();

    }

    private void setListener() {
        binding.tv1.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "1")));
        binding.tv2.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "2")));
        binding.tv3.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "3")));
        binding.tv4.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "4")));
        binding.tv5.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "5")));
        binding.tv6.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "6")));
        binding.tv7.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "7")));
        binding.tv8.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "8")));
        binding.tv9.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "9")));
        binding.tv0.setOnClickListener(v -> binding.etOTP.setText(String.format("%s%s", binding.etOTP.getText().toString(), "0")));
        binding.imgBackspace.setOnClickListener(v -> {
            if (binding.etOTP.getText().length() >= 1) {
                StringBuilder stringBuilder = new StringBuilder(binding.etOTP.getText());
                stringBuilder.deleteCharAt(binding.etOTP.getText().length()-1);
                String newString = stringBuilder.toString();
                binding.etOTP.setText(newString);
            }
        });

        binding.etOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 4) {
                    startActivity(new Intent(OTPActivity.this, ResetPasswordActivity.class));
                    finish();
                }
            }
        });

        binding.cvBack.setOnClickListener(v -> onBackPressed());
    }
}