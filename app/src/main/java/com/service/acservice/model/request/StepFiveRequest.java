package com.service.acservice.model.request;

import java.util.List;

public class StepFiveRequest {
    String appointment_id;
    List<String> images;

    public StepFiveRequest(String appointment_id, List<String> images) {
        this.appointment_id = appointment_id;
        this.images = images;
    }
}
