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

import com.google.android.material.snackbar.Snackbar;
import com.service.acservice.R;
import com.service.acservice.adapter.AdapterImages;
import com.service.acservice.databinding.FragmentImageSelectionBinding;
import com.service.acservice.listener.SelectedImageListener;
import com.service.acservice.model.request.ImagesModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectionFragment extends Fragment implements SelectedImageListener {
    FragmentImageSelectionBinding binding;
    private static final String TAG = "ImageFragment";
    List<String> selectedImagesList;
    AdapterImages selectedImageAdapter;
    ImagesModel selectedImageModel;
    public String encodedImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageSelectionBinding.inflate(getLayoutInflater(), container, false);
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

        selectedImageModel = new ImagesModel();
        selectedImagesList = new ArrayList<>();
        binding.rvSelectedImages.setHasFixedSize(true);
        selectedImageAdapter = new AdapterImages(selectedImagesList, this);
        binding.rvSelectedImages.setAdapter(selectedImageAdapter);
        binding.rvSelectedImages.setItemAnimator(new DefaultItemAnimator());
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