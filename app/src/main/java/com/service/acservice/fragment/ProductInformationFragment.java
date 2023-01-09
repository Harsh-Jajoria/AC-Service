package com.service.acservice.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.service.acservice.R;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.databinding.FragmentProductInformationBinding;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.StepOneRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductInformationFragment extends Fragment {
    FragmentProductInformationBinding binding;
    private String id, openDate;
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> brandAdapter;

    public ProductInformationFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductInformationBinding.inflate(getLayoutInflater(), container, false);
        setListener();
        fetchDropDown();
        fetchDetails();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnNextPage.setOnClickListener(v -> {
            if (isValid()) {
                submitStepOne();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            fetchDropDown();
            fetchDetails();
        });
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

    private void submitStepOne() {
        isLoading(true);
        try {
            StepOneRequest stepOneRequest = new StepOneRequest(
                    id,
                    binding.tvModel.getText().toString().trim(),
                    binding.tvCategory.getText().toString().trim(),
                    binding.actvBrand.getText().toString().trim()
            );

            ApiClient.getRetrofit().create(ApiService.class).submitStepOne(
                    stepOneRequest
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            isLoading(false);
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                            ((DetailsActivity) requireActivity()).setPage(2);
                        } else {
                            isLoading(false);
                            ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                        }
                    } else {
                        isLoading(false);
                        ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                    isLoading(false);
                    ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                }
            });

        } catch (Exception e) {
            isLoading(false);
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fetchDropDown() {
        try {
            ApiClient.getRetrofit().create(ApiService.class).getDropDown()
                    .enqueue(new Callback<DropDownResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<DropDownResponse> call, @NonNull Response<DropDownResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    categoryAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getProduct_category());
                                    binding.tvCategory.setAdapter(categoryAdapter);

                                    brandAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getBrands());
                                    binding.actvBrand.setAdapter(brandAdapter);

                                } else {
                                    ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                                }
                            } else {
                                ((DetailsActivity) requireActivity()).showToast("Something went wrong! Try Again");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DropDownResponse> call, @NonNull Throwable t) {
                            ((DetailsActivity) requireActivity()).showToast("Failed : " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
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
                            binding.tvModel.setText(response.body().data.get(0).getModel());
                            binding.tvCategory.setText(response.body().data.get(0).getProduct_category());
                            binding.actvBrand.setText(response.body().data.get(0).getBrand());
                            openDate = response.body().data.get(0).getOpen_date();
                            requestAgeCalculation();
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

    private void requestAgeCalculation() {
        String[] arrOfOpenDate = openDate.split("T");
        String newOpenData = arrOfOpenDate[0];
        String closeData = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date d1 = sdf.parse(newOpenData);
            Date d2 = sdf.parse(closeData);

            assert d2 != null;
            assert d1 != null;
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;
            binding.tvRequestAge.setText(String.format("%s", difference_In_Days));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Boolean isValid() {
        if (binding.tvModel.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Enter model");
            return false;
        } else if (binding.tvCategory.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select category");
            return false;
        } else if (binding.actvBrand.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select brand");
            return false;
        } else {
            return true;
        }
    }

}