package com.example.user.grabngo.Class;

public class Cart {

    private String productName, productRef, cartRef;
    private int quantity;

    public Cart() {
    }

    public Cart(String productName, String productRef, int quantity) {
        this.productName = productName;
        this.productRef = productRef;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductRef() {
        return productRef;
    }

    public void setProductRef(String productRef) {
        this.productRef = productRef;
    }

    public String getCartRef() {
        return cartRef;
    }

    public void setCartRef(String cartRef) {
        this.cartRef = cartRef;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
