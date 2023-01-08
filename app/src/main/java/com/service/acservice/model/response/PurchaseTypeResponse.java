package com.service.acservice.model.response;

import java.util.ArrayList;

public class PurchaseTypeResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public String name;

        public String getName() {
            return name;
        }
    }
}
