package com.service.acservice.listener;

import com.service.acservice.model.response.AppointmentDetailsResponse;

public interface AppointmentImageListener {
    void onImageClicked(AppointmentDetailsResponse.Datum.Attachment attachment);
}
