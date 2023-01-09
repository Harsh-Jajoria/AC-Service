package com.service.acservice.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.service.acservice.R;
import com.service.acservice.activities.AppointmentDetailsActivity;
import com.service.acservice.activities.DetailsActivity;
import com.service.acservice.activities.InvoiceActivity;
import com.service.acservice.adapter.AdapterImages;
import com.service.acservice.databinding.FragmentImageSelectionBinding;
import com.service.acservice.listener.SelectedImageListener;
import com.service.acservice.model.request.ImagesModel;
import com.service.acservice.model.request.StepFiveRequest;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageSelectionFragment extends Fragment implements SelectedImageListener {
    FragmentImageSelectionBinding binding;
    private static final String TAG = "ImageFragment";
    List<String> selectedImagesList;
    AdapterImages selectedImageAdapter;
    ImagesModel selectedImageModel;
    public String encodedImage, id, phone;

    // Firebase OTP
    BottomSheetDialog bottomSheetDialog;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    private EditText etOTP;

    public ImageSelectionFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageSelectionBinding.inflate(getLayoutInflater(), container, false);
        phone = ((DetailsActivity) requireActivity()).user_phone();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthCallback();
        setListener();
        return binding.getRoot();
    }

    private void setListener() {
        binding.imgSelectImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                imagePickerLauncher.launch(intent);
            } else {
                requestCameraPermission();
            }
        });

        binding.btnSubmit.setOnClickListener(v -> {
            startPhoneNumberVerification(phone);
            isLoading(true);
        });

        selectedImageModel = new ImagesModel();
        selectedImagesList = new ArrayList<>();
        binding.rvSelectedImages.setHasFixedSize(true);
        selectedImageAdapter = new AdapterImages(selectedImagesList, this);
        binding.rvSelectedImages.setAdapter(selectedImageAdapter);
        binding.rvSelectedImages.setItemAnimator(new DefaultItemAnimator());
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
                    submitStepFive();
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

    private void submitStepFive() {
        isLoading(true);
        try {
            StepFiveRequest stepFiveRequest = new StepFiveRequest(
                    id,
                    selectedImagesList
            );

            ApiClient.getRetrofit().create(ApiService.class).submitStepFive(
                    stepFiveRequest
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().code == 200) {
                            isLoading(false);
                            ((DetailsActivity) requireActivity()).showToast(response.body().status);
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

    private void isLoading(Boolean isLoading) {
        if (isLoading) {
            binding.btnText.setVisibility(View.GONE);
            binding.buttonProgress.setVisibility(View.VISIBLE);
        } else {
            binding.btnText.setVisibility(View.VISIBLE);
            binding.buttonProgress.setVisibility(View.GONE);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(binding.getRoot(), "Storage permission is required",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            Snackbar.make(binding.getRoot(), "No storage permission", Snackbar.LENGTH_LONG).show();
        }
    }

    ActivityResultLauncher<String> permissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    Log.d(TAG, ": Permission Granted");
                } else {
                    Log.d(TAG, ": Permission Not Granted");
                }
            }
    );

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int countOfImages = data.getClipData().getItemCount();
                            for (int i = 0; i < countOfImages; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    encodedImage = encodeImage(bitmap);
                                    selectedImagesList.add(encodedImage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            selectedImageAdapter.notifyDataSetChanged();
                        } else {
                            Uri imageUri = data.getData();
                            try {
                                InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                encodedImage = encodeImage(bitmap);
                                selectedImagesList.add(encodedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        selectedImageAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = bitmap.getWidth();
        int previewHeight = bitmap.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    public void onClicked(String encodedImage) {

    }

    @Override
    public void onDelete(String encodedImage, int position) {
        selectedImagesList.remove(position);
        selectedImageAdapter.notifyItemRemoved(position);
    }
}