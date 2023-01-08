package com.service.acservice.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.service.acservice.R;
import com.service.acservice.activities.AppointmentDetailsActivity;
import com.service.acservice.activities.MainActivity;
import com.service.acservice.adapter.AdapterTodayAppointment;
import com.service.acservice.databinding.FragmentCancelledAppointmentBinding;
import com.service.acservice.listener.TodayAppointmentListener;
import com.service.acservice.model.request.TodayAppointmentRequest;
import com.service.acservice.model.response.TodayAppointmentResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelledAppointmentFragment extends Fragment implements TodayAppointmentListener {
    FragmentCancelledAppointmentBinding binding;
    PreferenceManager preferenceManager;
    private final String status = "cancel";
    List<TodayAppointmentResponse.Datum> appointmentModelList = new ArrayList<>();
    AdapterTodayAppointment adapterTodayAppointment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCancelledAppointmentBinding.inflate(getLayoutInflater(), container, false);
        preferenceManager = new PreferenceManager(requireActivity());
        fetchAppointment();
        binding.shimmer.startShimmer();
        setListener();
        return binding.getRoot();
    }

    private void setListener () {
        binding.recyclerView.setHasFixedSize(true);
        appointmentModelList = new ArrayList<>();
        adapterTodayAppointment = new AdapterTodayAppointment(appointmentModelList, this);
        binding.recyclerView.setAdapter(adapterTodayAppointment);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            appointmentModelList.clear();
            fetchAppointment();
            binding.swipeRefresh.setRefreshing(false);
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAppointment() {
        try {
            TodayAppointmentRequest todayAppointmentRequest = new TodayAppointmentRequest(
                    preferenceManager.getString(Constants.USER_ID),
                    status
            );
            ApiClient.getRetrofit().create(ApiService.class)
                    .todayAppointment(todayAppointmentRequest).enqueue(new Callback<TodayAppointmentResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<TodayAppointmentResponse> call, @NonNull Response<TodayAppointmentResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    appointmentModelList.addAll(response.body().data);
                                    adapterTodayAppointment.notifyDataSetChanged();
                                    binding.swipeRefresh.setVisibility(View.VISIBLE);
                                    binding.shimmer.stopShimmer();
                                    binding.shimmer.setVisibility(View.GONE);
                                } else {
                                    ((MainActivity) requireActivity()).showSnackBar("No Data Found");
                                }
                            } else {
                                ((MainActivity) requireActivity()).showSnackBar("Something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TodayAppointmentResponse> call, @NonNull Throwable t) {
                            ((MainActivity) requireActivity()).showSnackBar("Check your internet connection");
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(requireActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void filter(String text) {
        List<TodayAppointmentResponse.Datum> filterList = new ArrayList<>();
        for (TodayAppointmentResponse.Datum item : appointmentModelList) {
            if (item.getAccount().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        adapterTodayAppointment.filterList(filterList);
    }

    @Override
    public void onAppointmentClick(TodayAppointmentResponse.Datum appointmentModel) {
        bottomSheet(appointmentModel.getSr_number(), appointmentModel.getRegistered_phone(), appointmentModel.getId());
    }

    private void bottomSheet(String serialNo, String phone, String id) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        View layoutView = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_appointment, null);
        bottomSheetDialog.setContentView(layoutView);
        bottomSheetDialog.setCancelable(false);
        TextView tvSerialNo = bottomSheetDialog.findViewById(R.id.tvSerialNo);
        MaterialCardView mcvCall = bottomSheetDialog.findViewById(R.id.mcvCall);
        MaterialButton btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);
        MaterialButton btnGotoScreen = bottomSheetDialog.findViewById(R.id.btnGotoScreen);

        assert tvSerialNo != null;
        tvSerialNo.setText(serialNo);
        assert btnCancel != null;
        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        assert btnGotoScreen != null;
        btnGotoScreen.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AppointmentDetailsActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        assert mcvCall != null;
        mcvCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });
        bottomSheetDialog.show();
    }

    public String getTitle() {
        return status.toUpperCase();
    }

}