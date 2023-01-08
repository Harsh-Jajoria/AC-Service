package com.service.acservice.model.response;

import java.util.ArrayList;

public class CategoryResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public String id;
        public String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}
