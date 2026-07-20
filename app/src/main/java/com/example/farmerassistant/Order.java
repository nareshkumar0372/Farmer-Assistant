package com.example.farmerassistant;

public class Order {

    private int id;
    private String productName;
    private int quantity;
    private double basePrice;
    private double finalPrice;
    private boolean isOrganic;
    private boolean bulkOrder;
    private boolean homeDelivery;

    public Order(int id, String productName, int quantity, double basePrice, double finalPrice,
                 boolean isOrganic, boolean bulkOrder, boolean homeDelivery) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
        this.isOrganic = isOrganic;
        this.bulkOrder = bulkOrder;
        this.homeDelivery = homeDelivery;
    }

    // Getters
    public int getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getBasePrice() { return basePrice; }
    public double getFinalPrice() { return finalPrice; }
    public boolean isOrganic() { return isOrganic; }
    public boolean isBulkOrder() { return bulkOrder; }
    public boolean isHomeDelivery() { return homeDelivery; }
}