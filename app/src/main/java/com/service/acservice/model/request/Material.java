package com.service.acservice.model.request;

public class Material {
    private String material, rate, quantity, total;

    public Material() {
    }

    public Material(String material, String rate, String quantity, String total) {
        this.material = material;
        this.rate = rate;
        this.quantity = quantity;
        this.total = total;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
