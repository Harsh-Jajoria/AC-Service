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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRemarksBinding.inflate(getLayoutInflater(), container, false);
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.fabNextPage.setOnClickListener(v -> ((DetailsActivity) requireActivity()).setPage(5));
        binding.tvClosedDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
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

}