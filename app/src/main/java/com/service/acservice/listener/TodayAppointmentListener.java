package com.service.acservice.listener;

import com.service.acservice.model.response.TodayAppointmentResponse;

public interface TodayAppointmentListener {
    void onAppointmentClick(TodayAppointmentResponse.Datum appointmentModel);
}
