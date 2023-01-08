package com.service.acservice.model.response;

import java.util.ArrayList;

public class DropDownResponse {
    public int code;
    public Data data;

    public class Data{
        public ArrayList<String> call_type;
        public ArrayList<String> status;
        public ArrayList<String> sub_status;
        public ArrayList<String> brands;
        public ArrayList<String> purchased_from;
        public ArrayList<String> product_category;
        public ArrayList<String> other;
        public ArrayList<String> material_type;

        public ArrayList<String> getCall_type() {
            return call_type;
        }

        public ArrayList<String> getStatus() {
            return status;
        }

        public ArrayList<String> getSub_status() {
            return sub_status;
        }

        public ArrayList<String> getBrands() {
            return brands;
        }

        public ArrayList<String> getPurchased_from() {
            return purchased_from;
        }

        public ArrayList<String> getProduct_category() {
            return product_category;
        }

        public ArrayList<String> getOther() {
            return other;
        }

        public ArrayList<String> getMaterial_type() {
            return material_type;
        }
    }
}
