package com.service.acservice.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.service.acservice.R;
import com.service.acservice.adapter.AdapterDetailsFragment;
import com.service.acservice.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding binding;
    AdapterDetailsFragment adapterDetailsFragment;
    String id, registered_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        registered_phone = intent.getStringExtra("phone");
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

    public String user_phone() {
        return registered_phone;
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

    public void loadingDialog(boolean isLoading) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layoutView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, findViewById(R.id.dialogRootView));
        builder.setView(layoutView);
        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        if (isLoading) {
            alertDialog.show();
        } else {
            alertDialog.dismiss();
        }
    }

}