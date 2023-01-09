package com.service.acservice.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.service.acservice.R;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.databinding.FragmentRemarksBinding;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.StepFourRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemarksFragment extends Fragment {
    FragmentRemarksBinding binding;
    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> subStatusAdapter;
    String id;

    public RemarksFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRemarksBinding.inflate(getLayoutInflater(), container, false);
        fetchDetails();
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnNextPage.setOnClickListener(v -> {
            if (isValid()) {
                submitStepFour();
            }
        });
        binding.tvClosedDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        binding.swipeRefresh.setOnRefreshListener(this::fetchDropDown);
    }

    private void fetchDetails() {
        binding.swipeRefresh.setRefreshing(true);
        try {

            AppointmentDetailsRequest appointmentDetailsRequest = new AppointmentDetailsRequest(id);

            ApiClient.getRetrofit().create(ApiService.class).appointmentDetails(
                    appointmentDetailsRequest
            ).enqueue(new Callback<AppointmentDetailsResponse>() {
                @Override
                public void onResponse(@NonNull Call<AppointmentDetailsResponse> call, @NonNull Response<AppointmentDetailsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            binding.actvStatus.setText(response.body().data.get(0).getStatus());
                            binding.actvSubStatus.setText(response.body().data.get(0).getSub_status());
                            binding.tvDescription.setText(response.body().data.get(0).getDescription());
                            binding.swipeRefresh.setRefreshing(false);
                        } else {
                            ((DetailsActivity) requireActivity()).showToast("Something went wrong!");
                            binding.swipeRefresh.setRefreshing(false);
                        }
                    } else {
                        ((DetailsActivity) requireActivity()).showToast("Something went wrong!");
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppointmentDetailsResponse> call, @NonNull Throwable t) {
                    ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void submitStepFour() {
        try {

            StepFourRequest stepFourRequest = new StepFourRequest(
                    id,
                    binding.tvClosedDate.getText().toString().trim(),
                    binding.actvStatus.getText().toString().trim(),
                    binding.actvSubStatus.getText().toString().trim(),
                    binding.tvDescription.getText().toString().trim()
            );

            ApiClient.getRetrofit().create(ApiService.class).submitStepFour(
                    stepFourRequest
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            ((DetailsActivity) requireActivity()).setPage(5);
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                            isLoading(false);
                        } else {
                            isLoading(false);
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                        }
                    } else {
                        isLoading(false);
                        ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try again");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                    isLoading(false);
                    ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            isLoading(false);
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
        }
    }

    private void fetchDropDown() {
        binding.swipeRefresh.setRefreshing(true);
        try {
            ApiClient.getRetrofit().create(ApiService.class).getDropDown()
                    .enqueue(new Callback<DropDownResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<DropDownResponse> call, @NonNull Response<DropDownResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    statusAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getStatus());
                                    binding.actvStatus.setAdapter(statusAdapter);

                                    subStatusAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getSub_status());
                                    binding.actvSubStatus.setAdapter(subStatusAdapter);

                                    binding.swipeRefresh.setRefreshing(false);

                                } else {
                                    ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                                    binding.swipeRefresh.setRefreshing(false);
                                }
                            } else {
                                ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                                binding.swipeRefresh.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DropDownResponse> call, @NonNull Throwable t) {
                            ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                            binding.swipeRefresh.setRefreshing(false);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private Boolean isValid() {
        if (binding.actvStatus.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select status");
            return false;
        } else if (binding.actvSubStatus.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select sub-status");
            return false;
        } else if (binding.tvClosedDate.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select closed date");
            return false;
        } else {
            return true;
        }
    }

    private void isLoading(Boolean isLoading) {
        if (isLoading) {
            binding.imgForward.setVisibility(View.GONE);
            binding.buttonProgress.setVisibility(View.VISIBLE);
        } else {
            binding.imgForward.setVisibility(View.VISIBLE);
            binding.buttonProgress.setVisibility(View.GONE);
        }
    }

}