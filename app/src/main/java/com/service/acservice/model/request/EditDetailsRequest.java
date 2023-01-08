package com.service.acservice.model.request;

import java.util.List;

public class EditDetailsRequest {
    private String model;
    private String category;
    private String brand;
    private String remark;
    private String serial;
    private String serial_split;
    private String tcr;
    private String tcr_date;
    private String purchased_from_fee;
    private String purchased_from;
    private String purchased_date;
    private String fee_amount;
    private String closed_date;
    private String appointment_id;
    //    private String products;
    private List<Material> products;
    private List<String> images;

    public EditDetailsRequest(String model, String category, String brand, String remark, String serial, String serial_split, String tcr, String tcr_date, String purchased_from_fee, String purchased_from, String purchased_date, String fee_amount, String closed_date, String appointment_id, List<Material> products, List<String> images) {
        this.model = model;
        this.category = category;
        this.brand = brand;
        this.remark = remark;
        this.serial = serial;
        this.serial_split = serial_split;
        this.tcr = tcr;
        this.tcr_date = tcr_date;
        this.purchased_from_fee = purchased_from_fee;
        this.purchased_from = purchased_from;
        this.purchased_date = purchased_date;
        this.fee_amount = fee_amount;
        this.closed_date = closed_date;
        this.appointment_id = appointment_id;
        this.products = products;
        this.images = images;
    }
}
