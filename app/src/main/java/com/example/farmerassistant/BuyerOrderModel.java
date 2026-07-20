package com.example.farmerassistant;

public class BuyerOrderModel {

    private String buyerName;
    private String productName;
    private int quantity;
    private double total;
    private double latitude;
    private double longitude;

    public BuyerOrderModel(String buyerName, String productName, int quantity,
                           double total, double latitude, double longitude) {
        this.buyerName = buyerName;
        this.productName = productName;
        this.quantity = quantity;
        this.total = total;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}