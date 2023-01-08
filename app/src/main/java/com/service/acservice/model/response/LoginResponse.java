package com.service.acservice.model.response;

public class LoginResponse {
    public int code;
    public String message;
    public Data data;

    public class Data{
        public String id;
        public String role;
        public String name;
        public String email;

        public String getId() {
            return id;
        }

        public String getRole() {
            return role;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

}
