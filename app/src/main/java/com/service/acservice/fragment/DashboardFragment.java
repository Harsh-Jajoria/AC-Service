package com.service.acservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.service.acservice.R;
import com.service.acservice.activities.MainActivity;
import com.service.acservice.databinding.FragmentDashboardBinding;
import com.service.acservice.model.request.CommonRequest;
import com.service.acservice.model.response.DashboardResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;
    PreferenceManager preferenceManager;
    TodayAppointmentFragment todayAppointmentFragment = new TodayAppointmentFragment();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(getLayoutInflater(), container, false);
        preferenceManager = new PreferenceManager(requireActivity());
        setListener();
        fetchDashboardData();
        return binding.getRoot();
    }

    private void setListener() {
        binding.cvTodayAppointment.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFragment(new TodayAppointmentFragment()));
        binding.cvAllAppointment.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFragment(new TotalAppointmentFragment()));
        binding.cvPendingAppointment.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFragment(new PendingAppointmentFragment()));
        binding.cvCancelledAppointment.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFragment(new CancelledAppointmentFragment()));
        binding.swipeRefresh.setOnRefreshListener(() -> {
            fetchDashboardData();
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void fetchDashboardData() {
        binding.shimmer.startShimmer();
        try {
            CommonRequest commonRequest = new CommonRequest(preferenceManager.getString(Constants.USER_ID));
            ApiClient.getRetrofit().create(ApiService.class)
                    .dashboard(commonRequest).enqueue(new Callback<DashboardResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    binding.tvTodayAppointment.setText(response.body().data.getToday());
                                    binding.tvPendingAppointment.setText(response.body().data.getPending());
                                    binding.tvAllAppointment.setText(response.body().data.getTotal());
                                    binding.tvCancelledAppointment.setText(response.body().data.getCancel());
                                    binding.shimmer.stopShimmer();
                                    binding.linearLayout.setVisibility(View.VISIBLE);
                                    binding.shimmer.setVisibility(View.GONE);
                                } else {
                                    ((MainActivity) requireActivity()).showSnackBar("Failed to fetch Information");
                                }
                            } else {
                                ((MainActivity) requireActivity()).showSnackBar("Failed to fetch Information");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                            ((MainActivity) requireActivity()).showSnackBar("Check your internet connection");
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}