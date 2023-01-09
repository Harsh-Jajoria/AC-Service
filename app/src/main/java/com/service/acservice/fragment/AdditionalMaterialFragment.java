package com.service.acservice.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.service.acservice.R;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.adapter.AdapterMaterial;
import com.service.acservice.databinding.FragmentAdditionalMaterialBinding;
import com.service.acservice.listener.MaterialListener;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.Material;
import com.service.acservice.model.request.StepThreeRequest;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.DropDownResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalMaterialFragment extends Fragment implements MaterialListener {
    FragmentAdditionalMaterialBinding binding;
    ArrayAdapter<String> callTypeAdapter;
    AutoCompleteTextView materialAutoCompleteTextView;
    private AdapterMaterial adapterMaterial;
    ArrayList<Material> materialArrayList = new ArrayList<>();
    ArrayAdapter<String> materialAdapter;
    String id;

    public AdditionalMaterialFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdditionalMaterialBinding.inflate(getLayoutInflater(), container, false);
        fetchDetails();
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnNextPage.setOnClickListener(v -> {
            if (isValid()) {
                submitStepThree();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(this::fetchDropDown);
        binding.imgMaterial.setOnClickListener(v -> additionalMaterialDialog());
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
                            binding.callTypeAutoComplete.setText(response.body().data.get(0).getCall_type());
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

    private void submitStepThree() {
        isLoading(true);
        try {
            StepThreeRequest stepThreeRequest = new StepThreeRequest(
                    id,
                    binding.callTypeAutoComplete.getText().toString().trim(),
                    binding.tvFeeAmount.getText().toString().trim(),
                    materialArrayList);

            ApiClient.getRetrofit().create(ApiService.class).submitStepThree(
                    stepThreeRequest
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                            ((DetailsActivity) requireActivity()).setPage(4);
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
            isLoading(false);
            ((DetailsActivity) requireActivity()).showToast("Error : " + e.getMessage());
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

    private void fetchDropDown() {
        binding.swipeRefresh.setRefreshing(true);
        try {
            ApiClient.getRetrofit().create(ApiService.class).getDropDown()
                    .enqueue(new Callback<DropDownResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<DropDownResponse> call, @NonNull Response<DropDownResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    callTypeAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getCall_type());
                                    binding.callTypeAutoComplete.setAdapter(callTypeAdapter);

                                    materialAdapter = new ArrayAdapter<>(
                                            requireActivity(),
                                            android.R.layout.simple_list_item_1,
                                            response.body().data.getMaterial_type());

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

    private void additionalMaterialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_additional_material, requireActivity().findViewById(R.id.dialogRootView));
        builder.setView(dialogView);

        TextInputEditText quantity = dialogView.findViewById(R.id.etQTY);
        TextInputEditText rate = dialogView.findViewById(R.id.etRate);
        TextView total = dialogView.findViewById(R.id.tvTotal);
        materialAutoCompleteTextView = dialogView.findViewById(R.id.actvMaterial);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAdd);

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!rate.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()) {
                    int a = Integer.parseInt(quantity.getText().toString());
                    int b = Integer.parseInt(rate.getText().toString());
                    int c = a * b;
//                    total.setText(String.format("Rs. %s", c));
                    total.setText("" + c);
                }
            }
        });

        materialAutoCompleteTextView.setAdapter(materialAdapter);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        btnAdd.setOnClickListener(v -> {
            if (materialAutoCompleteTextView.getText().toString().isEmpty()) {
                ((DetailsActivity) requireActivity()).showToast("Select material");
            } else if (rate.getText().toString().isEmpty()) {
                ((DetailsActivity) requireActivity()).showToast("Enter rate");
            } else if (quantity.getText().toString().isEmpty()) {
                ((DetailsActivity) requireActivity()).showToast("Enter quantity");
            } else {
                Material material = new Material();
                material.setMaterial(materialAutoCompleteTextView.getText().toString());
                material.setRate(rate.getText().toString());
                material.setQuantity(quantity.getText().toString());
                material.setTotal(total.getText().toString());
                materialArrayList.add(material);
                addMaterial();
                alertDialog.dismiss();
                binding.tvFeeAmount.setText("" + adapterMaterial.sum(materialArrayList));
            }
        });
        alertDialog.show();
    }

    private void addMaterial() {
        binding.additionalMaterialRecyclerView.setHasFixedSize(true);
        binding.additionalMaterialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapterMaterial = new AdapterMaterial(materialArrayList, this);
        binding.additionalMaterialRecyclerView.setAdapter(adapterMaterial);
        adapterMaterial.notifyItemInserted(materialArrayList.size());
    }

    @Override
    public void onDeleteClicked(Material material, int position) {
        materialArrayList.remove(position);
        binding.tvFeeAmount.setText("" + adapterMaterial.sum(materialArrayList));
        adapterMaterial.notifyItemRemoved(position);
        addMaterial();
    }

    private Boolean isValid() {
        if (binding.callTypeAutoComplete.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Select call type");
            return false;
        } else if (materialArrayList.isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Add material");
            return false;
        } else if (binding.tvFeeAmount.getText().toString().isEmpty()) {
            ((DetailsActivity) requireActivity()).showToast("Fee cannot be empty");
            return false;
        } else {
            return true;
        }
    }

}