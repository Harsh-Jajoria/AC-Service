package com.service.acservice.model.response;

import java.util.ArrayList;

public class MaterialResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public String material_name;

        public String getMaterial_name() {
            return material_name;
        }
    }
}
