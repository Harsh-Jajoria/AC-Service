package com.service.acservice.model.response;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

public class TodayAppointmentResponse {
    public int code;
    public ArrayList<Datum> data;

    @Entity(tableName = "AppointmentEntity")
    public class Datum{
        @PrimaryKey
        public String id;
        public String status;
        public String sub_status;
        public String account;
        public String registered_phone;
        public String description;
        public String owner;
        public String address;
        public String sr_number;

        public String getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public String getSub_status() {
            return sub_status;
        }

        public String getAccount() {
            return account;
        }

        public String getRegistered_phone() {
            return registered_phone;
        }

        public String getDescription() {
            return description;
        }

        public String getOwner() {
            return owner;
        }

        public String getAddress() {
            return address;
        }

        public String getSr_number() {
            return sr_number;
        }
    }
}
