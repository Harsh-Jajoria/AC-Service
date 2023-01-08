package com.service.acservice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.service.acservice.R;
import com.service.acservice.databinding.ActivityMainBinding;
import com.service.acservice.fragment.CancelledAppointmentFragment;
import com.service.acservice.fragment.DashboardFragment;
import com.service.acservice.fragment.PendingAppointmentFragment;
import com.service.acservice.fragment.TodayAppointmentFragment;
import com.service.acservice.fragment.TotalAppointmentFragment;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    PreferenceManager preferenceManager;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        replaceFragment(new DashboardFragment());
        setListener();
    }

    private void setListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
            if (f != null) {
                updateTitle(f);
            }
        });
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        binding.tvName.setText(preferenceManager.getString(Constants.USERNAME));
        binding.tvRole.setText(preferenceManager.getString(Constants.ROLE));
        binding.llProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            resultLauncher.launch(intent);
        });
    }

    private void updateTitle(Fragment fragment) {
        String fragClassName = fragment.getClass().getName();

        if (fragClassName.equals(TodayAppointmentFragment.class.getName())) {
            binding.tvTitle.setText("TODAY");
            binding.tvSubtitle.setText("Appointment");
            binding.tvSubtitle.setVisibility(View.VISIBLE);
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
            binding.toolbar.setNavigationIconTint(ContextCompat.getColor(MainActivity.this, R.color.primary_color));
        } else if (fragClassName.equals(PendingAppointmentFragment.class.getName())) {
            binding.tvTitle.setText("PENDING");
            binding.tvSubtitle.setText("Appointment");
            binding.tvSubtitle.setVisibility(View.VISIBLE);
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
            binding.toolbar.setNavigationIconTint(ContextCompat.getColor(MainActivity.this, R.color.primary_color));
        } else if (fragClassName.equals(CancelledAppointmentFragment.class.getName())) {
            binding.tvTitle.setText("CANCELLED");
            binding.tvSubtitle.setText("Appointment");
            binding.tvSubtitle.setVisibility(View.VISIBLE);
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
            binding.toolbar.setNavigationIconTint(ContextCompat.getColor(MainActivity.this, R.color.primary_color));
        } else if (fragClassName.equals(TotalAppointmentFragment.class.getName())) {
            binding.tvTitle.setText("ALL");
            binding.tvSubtitle.setText("Appointment");
            binding.tvSubtitle.setVisibility(View.VISIBLE);
            binding.toolbar.setNavigationIcon(R.drawable.ic_back);
            binding.toolbar.setNavigationIconTint(ContextCompat.getColor(MainActivity.this, R.color.primary_color));
        } else {
            binding.tvTitle.setText("Dashboard");
            binding.tvSubtitle.setVisibility(View.GONE);
            binding.toolbar.setNavigationIcon(null);
        }
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.mainContainer, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish();
                return;
            } else {
                Snackbar.make(binding.getRoot(), "Press back again to exit", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                        .setTextColor(ContextCompat.getColor(this, R.color.primary_color))
                        .show();
            }
            backPressedTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    public void showSnackBar(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.primary_color))
                .show();
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 12) {
                    Intent intent = result.getData();

                    if (intent != null) {
                        int code = intent.getIntExtra("finish", 0);
                        if (code == 1) {
                            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    }
                }
            });

}