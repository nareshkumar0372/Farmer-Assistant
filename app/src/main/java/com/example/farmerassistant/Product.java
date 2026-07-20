package com.example.farmerassistant;

public class Product {

    private int id;
    private String name;
    private String quantity;
    private String price;
    private String category;
    private byte[] image;
    private boolean isOrganic;
    private boolean bulkOrder;
    private boolean homeDelivery;

    public Product(int id, String name, String quantity, String price, String category, byte[] image,
                   boolean isOrganic, boolean bulkOrder, boolean homeDelivery) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.image = image;
        this.isOrganic = isOrganic;
        this.bulkOrder = bulkOrder;
        this.homeDelivery = homeDelivery;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public byte[] getImage() { return image; }
    public boolean isOrganic() { return isOrganic; }
    public boolean isBulkOrder() { return bulkOrder; }
    public boolean isHomeDelivery() { return homeDelivery; }
}