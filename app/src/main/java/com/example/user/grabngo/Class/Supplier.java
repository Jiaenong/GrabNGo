package com.example.user.grabngo.Class;

public class Supplier {

    private String name, email, phone, location, picUrl, documentId;

    public Supplier(){

    }

    public Supplier(String name, String email, String phone, String location, String picUrl, String documentId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.picUrl = picUrl;
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
