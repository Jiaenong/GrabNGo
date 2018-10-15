package com.example.user.grabngo.Class;

import java.util.List;

public class Payment {
    private String payDate;
    private String payTime;
    private List<String> item;
    private double totalPayment;

    public Payment()
    {

    }

    public Payment(String payDate, String payTime, List<String> item, double totalPayment)
    {
        this.payDate = payDate;
        this.payTime = payTime;
        this.item = item;
        this.totalPayment = totalPayment;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }
}
