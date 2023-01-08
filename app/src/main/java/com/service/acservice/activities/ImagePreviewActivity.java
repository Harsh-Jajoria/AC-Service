package com.service.acservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.service.acservice.databinding.ActivityImagePreviewBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImagePreviewActivity extends AppCompatActivity {
    ActivityImagePreviewBinding binding;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        url = "http://ac.thejcbschool.com/" + intent.getStringExtra("image");
        binding.imgPreview.setAlpha(0f);
        Picasso.get().load(url).into(binding.imgPreview, new Callback() {
            @Override
            public void onSuccess() {
                binding.imgPreview.animate().alpha(1f).setDuration(300).start();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}