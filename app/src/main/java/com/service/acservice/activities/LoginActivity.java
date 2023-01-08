package com.service.acservice.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.service.acservice.databinding.ActivityLoginBinding;
import com.service.acservice.model.request.LoginRequest;
import com.service.acservice.model.response.LoginResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    PreferenceManager preferenceManager;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        preferenceManager = new PreferenceManager(this);
        if (preferenceManager.getBoolean(Constants.isLogin)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setListener();
    }

    private void setListener() {
        binding.btnForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, OTPActivity.class)));
        binding.btnLayout.setOnClickListener(view -> {
            if (Objects.requireNonNull(binding.etEmail.getText()).toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                Toast.makeText(LoginActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(binding.etPassword.getText()).toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else {
                email = binding.etEmail.getText().toString().trim();
                password = binding.etPassword.getText().toString().trim();
                login();
            }
        });
    }

    private void login() {
        binding.btnText.setVisibility(View.GONE);
        binding.buttonProgress.setVisibility(View.VISIBLE);
        try {
            LoginRequest loginRequest = new LoginRequest(email, password);
            ApiClient.getRetrofit().create(ApiService.class).login(loginRequest)
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    binding.btnText.setVisibility(View.VISIBLE);
                                    binding.buttonProgress.setVisibility(View.GONE);
                                    preferenceManager.putString(Constants.USER_ID, response.body().data.getId());
                                    preferenceManager.putString(Constants.USERNAME, response.body().data.getName());
                                    preferenceManager.putString(Constants.EMAIL, response.body().data.getEmail());
                                    preferenceManager.putString(Constants.ROLE, response.body().data.getRole());
                                    preferenceManager.putBoolean(Constants.isLogin, true);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    binding.btnText.setVisibility(View.VISIBLE);
                                    binding.buttonProgress.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Something went wrong! Try Again", Toast.LENGTH_SHORT).show();
                                binding.btnText.setVisibility(View.VISIBLE);
                                binding.buttonProgress.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                            Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            binding.btnText.setVisibility(View.VISIBLE);
                            binding.buttonProgress.setVisibility(View.GONE);
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Login Failed : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.btnText.setVisibility(View.VISIBLE);
            binding.buttonProgress.setVisibility(View.GONE);
        }
    }

}