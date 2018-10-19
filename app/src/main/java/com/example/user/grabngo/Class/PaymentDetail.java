package com.example.user.grabngo.Class;

public class PaymentDetail {
    private String productName;
    private int quantity;
    private double itemPrice;

    public PaymentDetail()
    {

    }

    public PaymentDetail(String productName, int quantity, double itemPrice)
    {
        this.productName = productName;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
