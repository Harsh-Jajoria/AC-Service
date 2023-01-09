package com.service.acservice.model.request;

public class StepFourRequest {
    String appointment_id, closed_date, status, sub_status, remark;

    public StepFourRequest(String appointment_id, String closed_date, String status, String sub_status, String remark) {
        this.appointment_id = appointment_id;
        this.closed_date = closed_date;
        this.status = status;
        this.sub_status = sub_status;
        this.remark = remark;
    }
}
