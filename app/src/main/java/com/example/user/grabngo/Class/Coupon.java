package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


public class Coupon{
    private String title, description, type, status, code;
    private int cashRebate;
    private @ServerTimestamp Date startDate, endDate;

    public Coupon() {
    }

    public Coupon(String title, String description, String type, String status, String code, int cashRebate, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.code = code;
        this.cashRebate = cashRebate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCashRebate() {
        return cashRebate;
    }

    public void setCashRebate(int cashRebate) {
        this.cashRebate = cashRebate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
