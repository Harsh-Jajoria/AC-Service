package com.service.acservice.model.response;

import java.util.ArrayList;

public class SubStatusResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public int id;
        public String sub_status_name;

        public int getId() {
            return id;
        }

        public String getSub_status_name() {
            return sub_status_name;
        }
    }
}
