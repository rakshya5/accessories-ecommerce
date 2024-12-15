package com.example.ca.Controller;

public class AddToCartDTO {
    private Long productID;
    private int quantity;

    // Constructor
    public AddToCartDTO(Long productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
