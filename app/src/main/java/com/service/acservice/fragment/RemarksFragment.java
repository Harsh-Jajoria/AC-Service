package com.service.acservice.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.service.acservice.R;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.activities.InvoiceActivity;
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
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemarksFragment extends Fragment {
    FragmentRemarksBinding binding;
    private static final String TAG = "RemarksFragment";
    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> subStatusAdapter;
    String id, phone;

    // Firebase OTP
    BottomSheetDialog bottomSheetDialog;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    private EditText etOTP;

    public RemarksFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRemarksBinding.inflate(getLayoutInflater(), container, false);
        phone = ((DetailsActivity) requireActivity()).user_phone();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthCallback();
        fetchDetails();
        setListener();
        fetchDropDown();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnSubmit.setOnClickListener(v -> {
            if (isValid()) {
                startPhoneNumberVerification(phone);
                isLoading(true);
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
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
                            isLoading(false);
                            Intent intent = new Intent(requireContext(), InvoiceActivity.class);
                            intent.putExtra("invoice", response.body().getInvoice());
                            startActivity(intent);
                            requireActivity().finish();
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
            binding.btnText.setVisibility(View.GONE);
            binding.buttonProgress.setVisibility(View.VISIBLE);
        } else {
            binding.btnText.setVisibility(View.VISIBLE);
            binding.buttonProgress.setVisibility(View.GONE);
        }
    }

    // OTP Process Step By Step

    // Step 1 -> Verifying mobile number and sending otp
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Step 2 -> Checking if otp sent or not
    private void firebaseAuthCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(requireContext(), "Verification Failed :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent:" + s);
                mVerificationId = s;
                mForceResendingToken = forceResendingToken;
                isLoading(false);
                OTPBottomSheet();
            }
        };
    }

    // Step 3 -> If otp sent bottom sheet will show
    private void OTPBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetTheme);
        View layoutView = LayoutInflater.from(requireContext()).inflate(R.layout.bottomsheet_otp, null);
        bottomSheetDialog.setContentView(layoutView);

        etOTP = bottomSheetDialog.findViewById(R.id.etOTP);
        MaterialButton btnResend = bottomSheetDialog.findViewById(R.id.btnResendOTP);
        MaterialButton btnVerify = bottomSheetDialog.findViewById(R.id.btnVerify);
        ImageView imgClose = bottomSheetDialog.findViewById(R.id.imgClose);

        assert imgClose != null;
        imgClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        assert btnResend != null;
        btnResend.setOnClickListener(v -> resendVerificationCode(phone, mForceResendingToken));
        assert btnVerify != null;
        btnVerify.setOnClickListener(v -> verifyPhoneNumberWithCode(mVerificationId, etOTP.getText().toString()));
        ((View) layoutView.getParent()).setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    // Step 4 -> Veriyfing otp
    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Step 5 -> If otp matches, proceed
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    bottomSheetDialog.dismiss();
                    submitStepFour();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // For resending otp
    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber("+91" + phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}