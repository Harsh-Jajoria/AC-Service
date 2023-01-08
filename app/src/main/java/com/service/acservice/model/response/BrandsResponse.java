package com.service.acservice.model.response;

import java.util.ArrayList;

public class BrandsResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public int id;
        public String brand_name;

        public int getId() {
            return id;
        }

        public String getBrand_name() {
            return brand_name;
        }
    }
}
