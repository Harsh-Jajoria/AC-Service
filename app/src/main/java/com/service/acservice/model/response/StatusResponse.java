package com.service.acservice.model.response;

import java.util.ArrayList;

public class StatusResponse {
    public int code;
    public ArrayList<Datum> data;

    public class Datum{
        public int id;
        public String status_name;

        public int getId() {
            return id;
        }

        public String getStatus_name() {
            return status_name;
        }
    }

}
