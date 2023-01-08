package com.service.acservice.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.service.acservice.R;
import com.service.acservice.adapter.AdapterImages;
import com.service.acservice.adapter.AdapterMaterial;
import com.service.acservice.databinding.ActivityAppointmentDetailsBinding;
import com.service.acservice.listener.AppointmentImageListener;
import com.service.acservice.listener.MaterialListener;
import com.service.acservice.listener.SelectedImageListener;
import com.service.acservice.model.request.AppointmentDetailsRequest;
import com.service.acservice.model.request.EditDetailsRequest;
import com.service.acservice.model.request.ImagesModel;
import com.service.acservice.model.request.Material;
import com.service.acservice.model.response.AppointmentDetailsResponse;
import com.service.acservice.model.response.BrandsResponse;
import com.service.acservice.model.response.CallTypeResponse;
import com.service.acservice.model.response.CategoryResponse;
import com.service.acservice.model.response.CommonResponse;
import com.service.acservice.model.response.MaterialResponse;
import com.service.acservice.model.response.PurchaseFromResponse;
import com.service.acservice.model.response.PurchaseTypeResponse;
import com.service.acservice.model.response.StatusResponse;
import com.service.acservice.model.response.SubStatusResponse;
import com.service.acservice.network.ApiClient;
import com.service.acservice.network.ApiService;
import com.service.acservice.utils.Constants;
import com.service.acservice.utils.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailsActivity extends AppCompatActivity implements AppointmentImageListener, MaterialListener, SelectedImageListener {
    ActivityAppointmentDetailsBinding binding;
    PreferenceManager preferenceManager;
    private static final String TAG = "AppointmentActivity";
    private String id, allMaterials, serialImage, invoiceImage, partImage, otherImage, selectedImage;
    String empty = "----";
    AutoCompleteTextView materialAutoCompleteTextView;
    ArrayList<String> materialList = new ArrayList<>();
    ArrayAdapter<String> materialAdapter;

    ArrayList<String> callTypeList = new ArrayList<>();
    ArrayAdapter<String> callTypeAdapter;

    ArrayList<String> brandList = new ArrayList<>();
    ArrayAdapter<String> brandAdapter;

    ArrayList<String> purchaseFromList = new ArrayList<>();
    ArrayAdapter<String> purchaseFromAdapter;

    ArrayList<String> purchaseTypeList = new ArrayList<>();
    ArrayAdapter<String> purchaseTypeAdapter;

    ArrayList<String> categoryList = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;

    ArrayList<String> statusList = new ArrayList<>();
    ArrayAdapter<String> statusAdapter;

    ArrayList<String> subStatusList = new ArrayList<>();
    ArrayAdapter<String> subStatusAdapter;

    private EditText etOTP;

    private AdapterMaterial adapterMaterial;
    ArrayList<Material> materialArrayList = new ArrayList<>();

    // Firebase OTP
    BottomSheetDialog bottomSheetDialog;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    List<String> selectedImagesList;
    AdapterImages selectedImageAdapter;
    ImagesModel selectedImageModel;
    public String encodedImage;
    private static final int READ_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        binding.tvTechName.setText(preferenceManager.getString(Constants.USERNAME));
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.d("appointment", "onCreate: " + id);
        firebaseAuthCallback();
        setListener();
        fetchDetails();
        fetchCallType();
        fetchBrands();
        fetchPurchaseFrom();
        fetchPurchaseType();
        fetchCategory();
        fetchStatus();
        fetchSubStatus();

        // Setup for multi images
        selectedImageModel = new ImagesModel();
        selectedImagesList = new ArrayList<>();
        binding.rvSelectedImages.setHasFixedSize(true);
        selectedImageAdapter = new AdapterImages(selectedImagesList, this);
        binding.rvSelectedImages.setAdapter(selectedImageAdapter);
        binding.rvSelectedImages.setItemAnimator(new DefaultItemAnimator());

    }

    private void setListener() {
        binding.swipeRefresh.setOnRefreshListener(this::fetchDetails);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.tvClosedDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        binding.callTypeAutoComplete.setOnItemClickListener((adapterView, view, i, l) -> Toast.makeText(AppointmentDetailsActivity.this, binding.callTypeAutoComplete.getText().toString(), Toast.LENGTH_SHORT).show());

        binding.imgMaterial.setOnClickListener(v -> additionalMaterialDialog());

        binding.tvTCRDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getSupportFragmentManager(), "tag");

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

            datePicker.show(getSupportFragmentManager(), "tag");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                binding.tvPurchasedDate.setText(formattedDate);
            });
        });

        binding.tvClosedDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getSupportFragmentManager(), "tag");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                binding.tvClosedDate.setText(formattedDate);
            });
        });

        binding.btnSubmit.setOnClickListener(v -> {
            isSubmitLoading(true);
            startPhoneNumberVerification(binding.tvMobile.getText().toString());

        });

        binding.imgSelectImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                imagePickerLauncher.launch(intent);
            } else {
                requestCameraPermission();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDetails() {
        binding.swipeRefresh.setRefreshing(true);
        try {
            AppointmentDetailsRequest appointmentDetailsRequest = new AppointmentDetailsRequest(id);
            ApiClient.getRetrofit().create(ApiService.class)
                    .appointmentDetails(appointmentDetailsRequest)
                    .enqueue(new Callback<AppointmentDetailsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<AppointmentDetailsResponse> call, @NonNull Response<AppointmentDetailsResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    binding.swipeRefresh.setRefreshing(false);
                                    if (response.body().data.get(0).getId().isEmpty()) {
                                        binding.tvSerialNo.setText(empty);
                                    } else {
                                        binding.tvSerialNo.setText(response.body().data.get(0).getSr_number());
                                    }
                                    if (response.body().data.get(0).getAccount().isEmpty()) {
                                        binding.tvAccount.setText(empty);
                                    } else {
                                        binding.tvAccount.setText(response.body().data.get(0).getAccount());
                                    }
                                    if (response.body().data.get(0).getOpen_date().isEmpty()) {
                                        binding.tvOpenDate.setText(empty);
                                    } else {
                                        binding.tvOpenDate.setText(response.body().data.get(0).getOpen_date());
                                    }
                                    if (response.body().data.get(0).getAssign_date().isEmpty()) {
                                        binding.tvAssignDate.setText(empty);
                                    } else {
                                        binding.tvAssignDate.setText(response.body().data.get(0).getAssign_date());
                                    }
                                    if (response.body().data.get(0).getStatus().isEmpty()) {
                                        binding.tvStatus.setText(empty);
                                    } else {
                                        binding.tvStatus.setText(response.body().data.get(0).getStatus());
                                    }
                                    if (response.body().data.get(0).getSub_status().isEmpty()) {
                                        binding.tvSubStatus.setText(empty);
                                    } else {
                                        binding.tvSubStatus.setText(response.body().data.get(0).getSub_status());
                                    }
                                    if (response.body().data.get(0).getRegistered_phone().isEmpty()) {
                                        binding.tvMobile.setText(empty);
                                    } else {
                                        binding.tvMobile.setText(response.body().data.get(0).getRegistered_phone());
                                    }
                                    if (response.body().data.get(0).getRegistered_phone().isEmpty()) {
                                        binding.tvMobile.setText(empty);
                                    } else {
                                        binding.tvAltMobile.setText(response.body().data.get(0).getAlternate_phone());
                                    }
                                    if (response.body().data.get(0).getEmail().isEmpty()) {
                                        binding.tvEmail.setText(empty);
                                    } else {
                                        binding.tvEmail.setText(response.body().data.get(0).getEmail());
                                    }
                                    if (response.body().data.get(0).getAppointment_date().isEmpty()) {
                                        binding.tvAppointment.setText(empty);
                                    } else {
                                        binding.tvAppointment.setText(response.body().data.get(0).getAppointment_date());
                                    }
                                    if (!response.body().data.get(0).getModel().isEmpty()) {
                                        binding.tvModel.setText(response.body().data.get(0).getModel());
                                    }
                                    if (!response.body().data.get(0).getDescription().isEmpty()) {
                                        binding.tvDescription.setText(response.body().data.get(0).getDescription());
                                    }
                                    if (!response.body().data.get(0).getSerial().isEmpty()) {
                                        binding.tvSerial.setText(response.body().data.get(0).getSerial());
                                    }
                                    if (!response.body().data.get(0).getSerial_split().isEmpty()) {
                                        binding.tvSerialSplit.setText(response.body().data.get(0).getSerial_split());
                                    }
                                    if (!response.body().data.get(0).getProduct_category().isEmpty()) {
                                        binding.tvCategory.setText(response.body().data.get(0).getProduct_category());
                                    }
                                    if (!response.body().data.get(0).getTcr_date().isEmpty()) {
                                        binding.tvTCRDate.setText(response.body().data.get(0).getTcr_date());
                                    }
                                    if (!response.body().data.get(0).getPurchased_from().isEmpty()) {
                                        binding.tvPurchasedFrom.setText(response.body().data.get(0).getPurchased_from());
                                    }
                                    if (!response.body().data.get(0).getPurchased_from_free().isEmpty()) {
                                        binding.tvPurchasedFromFee.setText(response.body().data.get(0).getPurchased_from_free());
                                    }
                                    if (response.body().data.get(0).getPurchased_date().isEmpty()) {
                                        binding.tvPurchasedDate.setText(empty);
                                    } else {
                                        binding.tvPurchasedDate.setText(response.body().data.get(0).getPurchased_date());
                                    }
                                    if (!response.body().data.get(0).getFee_amount().isEmpty()) {
                                        binding.tvFeeAmount.setText(response.body().data.get(0).getFee_amount());
                                    }
                                    if (response.body().data.get(0).getAddress().isEmpty()) {
                                        binding.tvAddress.setText(empty);
                                    } else {
                                        binding.tvAddress.setText(response.body().data.get(0).getAddress());
                                    }
                                    if (response.body().data.get(0).getArea().isEmpty()) {
                                        binding.tvArea.setText(empty);
                                    } else {
                                        binding.tvArea.setText(response.body().data.get(0).getArea());
                                    }
//                                    binding.tvState.setText(empty);
                                    binding.tvCity.setText(empty);
                                    if (response.body().data.get(0).getPincode().isEmpty()) {
                                        binding.tvPinCode.setText(empty);
                                    } else {
                                        binding.tvPinCode.setText(response.body().data.get(0).getPincode());
                                    }
                                    if (response.body().data.get(0).getVip().isEmpty()) {
                                        binding.tvVIP.setText(empty);
                                    } else {
                                        binding.tvVIP.setText(response.body().data.get(0).getVip());
                                    }
                                    requestAgeCalculation();
                                    makeTCR(response.body().data.get(0).getSr_number());
                                } else {
                                    binding.swipeRefresh.setRefreshing(false);
                                    showSnackBar("No Data Found");
                                }
                            } else {
                                binding.swipeRefresh.setRefreshing(false);
                                showSnackBar("Something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<AppointmentDetailsResponse> call, @NonNull Throwable t) {
                            binding.swipeRefresh.setRefreshing(false);
                            showSnackBar("Check your internet connection");
                        }
                    });
        } catch (Exception e) {
            binding.swipeRefresh.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeTCR(String sr_number) {
        String partA = sr_number.substring(0, 2);
        String partB = sr_number.substring(10, 12);
        binding.tvTCR.setText(String.format("%s%s", partA, partB));
    }

    private void fetchMaterial() {
        try {

            ApiClient.getRetrofit().create(ApiService.class)
                    .getMaterial().enqueue(new Callback<MaterialResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MaterialResponse> call, @NonNull Response<MaterialResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String material = response.body().data.get(i).getMaterial_name();
                                        materialList.add(material);
                                    }

                                    materialAdapter = new ArrayAdapter<>(AppointmentDetailsActivity.this, android.R.layout.simple_list_item_1, materialList);
                                    materialAutoCompleteTextView.setAdapter(materialAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MaterialResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCallType() {
        try {

            ApiClient.getRetrofit().create(ApiService.class)
                    .getCallType().enqueue(new Callback<CallTypeResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CallTypeResponse> call, @NonNull Response<CallTypeResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String callType = response.body().data.get(i).getCall_type_name();
                                        callTypeList.add(callType);
                                    }

                                    callTypeAdapter = new ArrayAdapter<>(AppointmentDetailsActivity.this, android.R.layout.simple_list_item_1, callTypeList);
                                    binding.callTypeAutoComplete.setAdapter(callTypeAdapter);

                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CallTypeResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBrands() {
        try {
            ApiClient.getRetrofit().create(ApiService.class)
                    .getBrands().enqueue(new Callback<BrandsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<BrandsResponse> call, @NonNull Response<BrandsResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String brand = response.body().data.get(i).getBrand_name();
                                        brandList.add(brand);
                                    }
                                    brandAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, brandList);
                                    binding.actvBrand.setAdapter(brandAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<BrandsResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchPurchaseType() {
        try {
            ApiClient.getRetrofit().create(ApiService.class).getPurchaseType()
                    .enqueue(new Callback<PurchaseTypeResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PurchaseTypeResponse> call, @NonNull Response<PurchaseTypeResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String brand = response.body().data.get(i).getName();
                                        purchaseTypeList.add(brand);
                                    }

                                    purchaseTypeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, purchaseTypeList);
                                    binding.tvPurchasedType.setAdapter(purchaseTypeAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PurchaseTypeResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchPurchaseFrom() {
        try {

            ApiClient.getRetrofit().create(ApiService.class)
                    .getPurchaseFrom()
                    .enqueue(new Callback<PurchaseFromResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PurchaseFromResponse> call, @NonNull Response<PurchaseFromResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String brand = response.body().data.get(i).getName();
                                        purchaseFromList.add(brand);
                                    }
                                    purchaseFromAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, purchaseFromList);
                                    binding.tvPurchasedFrom.setAdapter(purchaseFromAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PurchaseFromResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchCategory() {
        try {

            ApiClient.getRetrofit().create(ApiService.class).getCategory()
                    .enqueue(new Callback<CategoryResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String cat = response.body().data.get(i).getName();
                                        categoryList.add(cat);
                                    }
                                    categoryAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, categoryList);
                                    binding.tvCategory.setAdapter(categoryAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchSubStatus() {
        try {

            ApiClient.getRetrofit().create(ApiService.class).getSubStatus()
                    .enqueue(new Callback<SubStatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<SubStatusResponse> call, @NonNull Response<SubStatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String subStatus = response.body().data.get(i).getSub_status_name();
                                        subStatusList.add(subStatus);
                                    }

                                    subStatusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, subStatusList);
                                    binding.actvSubStatus.setAdapter(subStatusAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<SubStatusResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchStatus() {
        try {

            ApiClient.getRetrofit().create(ApiService.class).getStatus()
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        String status = response.body().data.get(i).getStatus_name();
                                        statusList.add(status);
                                    }
                                    statusAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, statusList);
                                    binding.actvStatus.setAdapter(statusAdapter);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isSubmitLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnSubmit.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onImageClicked(AppointmentDetailsResponse.Datum.Attachment attachment) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("image", attachment.getImage());
        startActivity(intent);
    }

    private void additionalMaterialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_additional_material, findViewById(R.id.dialogRootView));
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

        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        btnAdd.setOnClickListener(v -> {
            if (materialAutoCompleteTextView.getText().toString().isEmpty()) {
                showToast("Select material");
            } else if (rate.getText().toString().isEmpty()) {
                showToast("Enter rate");
            } else if (quantity.getText().toString().isEmpty()) {
                showToast("Enter quantity");
            } else {
                materialList.clear();
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
        fetchMaterial();
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

    // OTP Process Step By Step

    // Step 1 -> Verifying mobile number and sending otp
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
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
                Toast.makeText(getApplicationContext(), "Verification Failed :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent:" + s);
                mVerificationId = s;
                mForceResendingToken = forceResendingToken;
                isSubmitLoading(false);
                OTPBottomSheet();
            }
        };
    }

    // Step 3 -> If otp sent bottom sheet will show
    private void OTPBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        View layoutView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_otp, null);
        bottomSheetDialog.setContentView(layoutView);

        etOTP = bottomSheetDialog.findViewById(R.id.etOTP);
        MaterialButton btnResend = bottomSheetDialog.findViewById(R.id.btnResendOTP);
        MaterialButton btnVerify = bottomSheetDialog.findViewById(R.id.btnVerify);
        ImageView imgClose = bottomSheetDialog.findViewById(R.id.imgClose);

        assert imgClose != null;
        imgClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        assert btnResend != null;
        btnResend.setOnClickListener(v -> resendVerificationCode(binding.tvMobile.getText().toString(), mForceResendingToken));
        assert btnVerify != null;
        btnVerify.setOnClickListener(v -> verifyPhoneNumberWithCode(mVerificationId, etOTP.getText().toString()));
        ((View) layoutView.getParent()).setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
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
                    Gson gson = new Gson();
                    allMaterials = gson.toJson(materialArrayList);
                    Log.d(TAG, "onSuccess: " + allMaterials);
                    Gson gson1 = new Gson();
                    selectedImage = gson1.toJson(selectedImagesList);
                    Log.d(TAG, "setListener: " + selectedImage);
                    editDetails();
                    bottomSheetDialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(AppointmentDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // For resending otp
    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber("+91" + phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void showSnackBar(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.primary_color))
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestAgeCalculation() {
        String openDate = binding.tvOpenDate.getText().toString();
        String[] arrOfOpenDate = openDate.split("T");

        String newOpenData = arrOfOpenDate[0];
        String closeData = binding.tvClosedDate.getText().toString();
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
            binding.tvRequestAge.setText(String.format("%s day", difference_In_Days));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = bitmap.getWidth();
        int previewHeight = bitmap.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

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
                                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
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
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
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

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
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

    @Override
    public void onClicked(String encodedImage) {

    }

    @Override
    public void onDelete(String encodedImage, int position) {
        selectedImagesList.remove(position);
        selectedImageAdapter.notifyItemRemoved(position);
    }

    private void editDetails() {
        try {

            EditDetailsRequest editDetailsRequest = new EditDetailsRequest(
                    binding.tvModel.getText().toString(),
                    binding.tvCategory.getText().toString(),
                    binding.actvBrand.getText().toString(),
                    binding.tvDescription.getText().toString(),
                    binding.tvSerial.getText().toString(),
                    binding.tvSerialSplit.getText().toString(),
                    binding.tvTCR.getText().toString(),
                    binding.tvTCRDate.getText().toString(),
                    binding.tvPurchasedFromFee.getText().toString(),
                    binding.tvPurchasedFrom.getText().toString(),
                    binding.tvPurchasedDate.getText().toString(),
                    binding.tvFeeAmount.getText().toString(),
                    binding.tvClosedDate.getText().toString(),
                    id,
                    materialArrayList,
                    selectedImagesList
            );

            ApiClient.getRetrofit().create(ApiService.class).editDetails(editDetailsRequest)
                    .enqueue(new Callback<CommonResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().code == 200) {
                                    Toast.makeText(AppointmentDetailsActivity.this, response.body().status, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AppointmentDetailsActivity.this, InvoiceActivity.class);
                                    intent.putExtra("invoice", response.body().getInvoice());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(AppointmentDetailsActivity.this, response.body().status, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AppointmentDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                            Toast.makeText(AppointmentDetailsActivity.this, "Failed : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AppointmentDetailsActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

// serial no., invoice, Part, Others