package com.example.user.grabngo.Class;

public class RefundTemp {

    private String productName, customerName, day, monthYear, time, documentID;

    public RefundTemp(){

    }

    public RefundTemp(String productName, String customerName, String day, String monthYear, String time, String documentID){
        this.productName = productName;
        this.customerName = customerName;
        this.day = day;
        this.monthYear = monthYear;
        this.time = time;
        this.documentID = documentID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
