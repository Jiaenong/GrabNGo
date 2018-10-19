package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Refund {
    private String productName, customerName, imgUrl, reason, modifiedStaff;
    private @ServerTimestamp Date refundDate, modifiedDate;

    public Refund() {
    }

    public Refund(String customerName, String imgUrl, Date modifiedDate, String modifiedStaff, String productName, String reason, Date refundDate) {
        this.productName = productName;
        this.customerName = customerName;
        this.imgUrl = imgUrl;
        this.reason = reason;
        this.modifiedStaff = modifiedStaff;
        this.refundDate = refundDate;
        this.modifiedDate = modifiedDate;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getModifiedStaff() {
        return modifiedStaff;
    }

    public void setModifiedStaff(String modifiedStaff) {
        this.modifiedStaff = modifiedStaff;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
