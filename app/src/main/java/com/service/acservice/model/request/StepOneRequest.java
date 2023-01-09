package com.service.acservice.model.request;

public class StepOneRequest {
    String appointment_id, model, category, brand;

    public StepOneRequest(String appointment_id, String model, String category, String brand) {
        this.appointment_id = appointment_id;
        this.model = model;
        this.category = category;
        this.brand = brand;
    }
}
