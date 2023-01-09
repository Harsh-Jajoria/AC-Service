package com.service.acservice.model.request;

import java.util.List;

public class StepThreeRequest {
    String appointment_id,call_type, fee_amount;
    List<Material> products;

    public StepThreeRequest(String appointment_id, String call_type, String fee_amount, List<Material> products) {
        this.appointment_id = appointment_id;
        this.call_type = call_type;
        this.fee_amount = fee_amount;
        this.products = products;
    }
}
