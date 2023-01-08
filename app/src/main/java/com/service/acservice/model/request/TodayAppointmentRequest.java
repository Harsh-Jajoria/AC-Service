package com.service.acservice.model.request;

public class TodayAppointmentRequest {
    private String user_id;
    private String status;

    public TodayAppointmentRequest(String user_id, String status) {
        this.user_id = user_id;
        this.status = status;
    }
}
