package com.service.acservice.model.response;

public class DashboardResponse {
    public int code;
    public Data data;

    public class Data{
        public String today;
        public String total;
        public String pending;

        public String getToday() {
            return today;
        }

        public String getTotal() {
            return total;
        }

        public String getPending() {
            return pending;
        }
    }

}
