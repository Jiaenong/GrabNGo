package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Payment {
    private @ServerTimestamp Date payDate;
    private double totalPayment;
    private String customerKey;

    public Payment()
    {

    }

    public Payment(Date payDate, double totalPayment, String customerKey)
    {
        this.payDate = payDate;
        this.totalPayment = totalPayment;
        this.customerKey = customerKey;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }
}
