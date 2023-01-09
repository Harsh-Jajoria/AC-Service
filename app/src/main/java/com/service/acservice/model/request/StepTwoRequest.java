package com.service.acservice.model.request;

public class StepTwoRequest {
    String appointment_id, serial, serial_split, tcr, tcr_date, purchased_from, purchased_type, purchased_date;

    public StepTwoRequest(String appointment_id, String serial, String serial_split, String tcr, String tcr_date, String purchased_from, String purchased_type, String purchased_date) {
        this.appointment_id = appointment_id;
        this.serial = serial;
        this.serial_split = serial_split;
        this.tcr = tcr;
        this.tcr_date = tcr_date;
        this.purchased_from = purchased_from;
        this.purchased_type = purchased_type;
        this.purchased_date = purchased_date;
    }
}
