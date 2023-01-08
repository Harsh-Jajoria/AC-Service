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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPurchaseInformationBinding.inflate(getLayoutInflater(), container, false);
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.fabNextPage.setOnClickListener(v -> {
            if (isValid()) {
                ((DetailsActivity) requireActivity()).setPage(3);
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

}