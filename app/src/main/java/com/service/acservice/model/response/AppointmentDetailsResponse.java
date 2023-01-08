package com.service.acservice.model.response;

import java.util.ArrayList;

public class AppointmentDetailsResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public String id;
        public String sr_number;
        public String sub_status;
        public String account;
        public String registered_phone;
        public String alternate_phone;
        public String description;
        public String owner;
        public String address;
        public String status;
        public String email;
        public String closer_code;
        public String vip;
        public String closed_date;
        public String serial_split;
        public String serial;
        public String model;
        public String fee_amount;
        public String open_date;
        public String tcr_date;
        public String tcr;
        public String unit_status;
        public String appointment_date;
        public String processing_status;
        public String organization;
        public String assign_date;
        public String age;
        public String product_group;
        public String product_category;
        public String call_type;
        public String area;
        public String purchased_date;
        public String purchased_from_free;
        public String purchased_from;
        public String pincode;
        public ArrayList<Attachment> attachment;

        public String getId() {
            return id;
        }

        public String getSr_number() {
            return sr_number;
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

        public String getAlternate_phone() {
            return alternate_phone;
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

        public String getStatus() {
            return status;
        }

        public String getEmail() {
            return email;
        }

        public String getCloser_code() {
            return closer_code;
        }

        public String getVip() {
            return vip;
        }

        public String getClosed_date() {
            return closed_date;
        }

        public String getSerial_split() {
            return serial_split;
        }

        public String getSerial() {
            return serial;
        }

        public String getModel() {
            return model;
        }

        public String getFee_amount() {
            return fee_amount;
        }

        public String getOpen_date() {
            return open_date;
        }

        public String getTcr_date() {
            return tcr_date;
        }

        public String getTcr() {
            return tcr;
        }

        public String getUnit_status() {
            return unit_status;
        }

        public String getAppointment_date() {
            return appointment_date;
        }

        public String getProcessing_status() {
            return processing_status;
        }

        public String getOrganization() {
            return organization;
        }

        public String getAssign_date() {
            return assign_date;
        }

        public String getAge() {
            return age;
        }

        public String getProduct_group() {
            return product_group;
        }

        public String getProduct_category() {
            return product_category;
        }

        public String getCall_type() {
            return call_type;
        }

        public String getArea() {
            return area;
        }

        public String getPurchased_date() {
            return purchased_date;
        }

        public String getPincode() {
            return pincode;
        }

        public String getPurchased_from_free() {
            return purchased_from_free;
        }

        public String getPurchased_from() {
            return purchased_from;
        }

        public ArrayList<Attachment> getAttachment() {
            return attachment;
        }

        public class Attachment{
            public String image;

            public String getImage() {
                return image;
            }
        }

    }
}
