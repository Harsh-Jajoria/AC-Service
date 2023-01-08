package com.service.acservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.databinding.FragmentPersonalInformationBinding;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationFragment extends Fragment {
    FragmentPersonalInformationBinding binding;
    PreferenceManager preferenceManager;
    private String id;

    public PersonalInformationFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPersonalInformationBinding.inflate(getLayoutInflater(), container, false);
        preferenceManager = new PreferenceManager(requireActivity());
        setListener();
        fetchPersonalInformation();
        return binding.getRoot();
    }

    private void setListener() {
        binding.swipeRefresh.setOnRefreshListener(this::fetchPersonalInformation);
        binding.fabNextPage.setOnClickListener(v -> ((DetailsActivity) requireActivity()).setPage(1));
    }

    private void fetchPersonalInformation() {
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
                            binding.tvSerialNo.setText(response.body().data.get(0).getSr_number());
                            binding.tvAccount.setText(response.body().data.get(0).getAccount());
                            binding.tvOpenDate.setText(response.body().data.get(0).getOpen_date());
                            binding.tvAssignDate.setText(response.body().data.get(0).getAssign_date());
                            binding.tvStatus.setText(response.body().data.get(0).getStatus());
                            binding.tvSubStatus.setText(response.body().data.get(0).getSub_status());
                            binding.tvMobile.setText(response.body().data.get(0).getRegistered_phone());
                            binding.tvAltMobile.setText(response.body().data.get(0).getAlternate_phone());
                            binding.tvEmail.setText(response.body().data.get(0).getEmail());
                            binding.tvTechName.setText(preferenceManager.getString(Constants.USERNAME));
                            binding.tvAppointment.setText(response.body().data.get(0).getAppointment_date());
                            binding.tvAddress.setText(response.body().data.get(0).getAddress());
                            binding.tvArea.setText(response.body().data.get(0).getArea());
                            binding.tvPinCode.setText(response.body().data.get(0).getPincode());
                            binding.tvVIP.setText(response.body().data.get(0).getVip());
                            binding.swipeRefresh.setRefreshing(false);
                        } else {
                            showToast("Something went wrong!");
                            binding.swipeRefresh.setRefreshing(false);
                        }
                    } else {
                        showToast("Something went wrong!");
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppointmentDetailsResponse> call, @NonNull Throwable t) {
                    showToast("Failed : " + t.getMessage());
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            showToast("Error : " + e.getMessage());
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

}