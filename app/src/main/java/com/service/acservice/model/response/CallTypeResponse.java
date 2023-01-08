package com.service.acservice.model.response;

import java.util.ArrayList;

public class CallTypeResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public int id;
        public String call_type_name;

        public int getId() {
            return id;
        }

        public String getCall_type_name() {
            return call_type_name;
        }
    }
}
