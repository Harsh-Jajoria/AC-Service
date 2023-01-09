package com.service.acservice.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.service.acservice.R;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.databinding.FragmentPurchaseInformationBinding;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.StepTwoRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseInformationFragment extends Fragment {
    FragmentPurchaseInformationBinding binding;
    ArrayAdapter<String> purchasedType;
    ArrayAdapter<String> purchasedFrom;
    String id;

    public PurchaseInformationFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPurchaseInformationBinding.inflate(getLayoutInflater(), container, false);
        fetchDetails();
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnNextPage.setOnClickListener(v -> {
            if (isValid()) {
                submitStep2();
            }
        });

        binding.tvTCRDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(requireActivity().getSupportFragmentManager(), "tag");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                binding.tvTCRDate.setText(formattedDate);
            });
        });

        binding.tvPurchasedDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(requireActivity().getSupportFragmentManager(), "tag");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                binding.tvPurchasedDate.setText(formattedDate);
            });
        });

        binding.swipeRefresh.setOnRefreshListener(this::fetchDropDown);
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
                                    purchasedType = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getProduct_category());
                                    binding.tvPurchasedType.setAdapter(purchasedType);

                                    purchasedFrom = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getPurchased_from());
                                    binding.tvPurchasedFrom.setAdapter(purchasedFrom);
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

    private void submitStep2() {
        isLoading(true);
        try {
            StepTwoRequest stepTwoRequest = new StepTwoRequest(
                    id,
                    binding.tvSerial.getText().toString().trim(),
                    binding.tvSerialSplit.getText().toString().trim(),
                    binding.tvTCR.getText().toString().trim(),
                    binding.tvTCRDate.getText().toString().trim(),
                    binding.tvPurchasedFrom.getText().toString().trim(),
                    binding.tvPurchasedType.getText().toString().trim(),
                    binding.tvPurchasedDate.getText().toString().trim()
            );

            ApiClient.getRetrofit().create(ApiService.class).submitStepTwo(
                    stepTwoRequest
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            ((DetailsActivity) requireActivity()).setPage(3);
                            isLoading(false);
                        } else {
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                            isLoading(false);
                        }
                    } else {
                        ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try again");
                        isLoading(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                    ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                    isLoading(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
            isLoading(false);
        }
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
                            binding.tvSerial.setText(response.body().data.get(0).getSerial());
                            binding.tvSerialSplit.setText(response.body().data.get(0).getSerial_split());
                            binding.tvTCR.setText(response.body().data.get(0).getTcr());
                            binding.tvTCRDate.setText(response.body().data.get(0).getTcr_date());
                            binding.tvPurchasedFrom.setText(response.body().data.get(0).getPurchased_from());
                            binding.tvPurchasedDate.setText(response.body().data.get(0).getPurchased_date());
                            binding.tvPurchasedType.setText(response.body().data.get(0).getPurchased_type());
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

    private void isLoading(Boolean isLoading) {
        if (isLoading) {
            binding.imgForward.setVisibility(View.GONE);
            binding.buttonProgress.setVisibility(View.VISIBLE);
        } else {
            binding.imgForward.setVisibility(View.VISIBLE);
            binding.buttonProgress.setVisibility(View.GONE);
        }
    }

    private Boolean isValid() {
        if (binding.tvSerial.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter serial");
            return false;
        } else if (binding.tvSerialSplit.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter serial split");
            return false;
        } else if (binding.tvTCR.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter TCR");
            return false;
        } else if (binding.tvTCRDate.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter TCR date");
            return false;
        } else if (binding.tvPurchasedType.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter purchase type");
            return false;
        } else if (binding.tvPurchasedFrom.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter purchase from");
            return false;
        } else if (binding.tvPurchasedDate.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter purchase date");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DetailsActivity) requireActivity()).loadingDialog(false);
    }
}