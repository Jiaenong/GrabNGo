package com.example.user.grabngo.Class;

public class CartItem {

    private String productname;
    private String imageSrc;
    private int quantity;
    private double price;

    public CartItem(String productname, String imageSrc, int quantity, double price) {
        this.productname = productname;
        this.imageSrc = imageSrc;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
