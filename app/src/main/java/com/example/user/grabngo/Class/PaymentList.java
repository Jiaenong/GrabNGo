package com.example.user.grabngo.Class;

public class PaymentList {
    private String productName;
    private int productNum;

    public PaymentList()
    {

    }

    public PaymentList(String productName, int productNum)
    {
        this.productName = productName;
        this.productNum = productNum;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }
}
