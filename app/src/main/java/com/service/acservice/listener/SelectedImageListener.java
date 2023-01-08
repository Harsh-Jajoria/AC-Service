package com.service.acservice.listener;

public interface SelectedImageListener {
    void onClicked(String encodedImage);

    void onDelete(String encodedImage, int position);
}
