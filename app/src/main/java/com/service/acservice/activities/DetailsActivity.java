package com.service.acservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.service.acservice.adapter.AdapterDetailsFragment;
import com.service.acservice.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding binding;
    AdapterDetailsFragment adapterDetailsFragment;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        adapterDetailsFragment = new AdapterDetailsFragment(this, id);
        binding.viewPager2.setAdapter(adapterDetailsFragment);
        binding.viewPager2.setUserInputEnabled(false);
    }

    public void setPage(int index) {
        binding.viewPager2.setCurrentItem(index);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (binding.viewPager2.getCurrentItem() == 6) {
            setPage(5);
        } else if (binding.viewPager2.getCurrentItem() == 5) {
            setPage(4);
        } else if (binding.viewPager2.getCurrentItem() == 4) {
            setPage(3);
        } else if (binding.viewPager2.getCurrentItem() == 3) {
            setPage(2);
        } else if (binding.viewPager2.getCurrentItem() == 2) {
            setPage(1);
        } else if (binding.viewPager2.getCurrentItem() == 1) {
            setPage(0);
        } else {
            super.onBackPressed();
        }
    }
}