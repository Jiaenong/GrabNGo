package com.example.user.grabngo.Class;

import java.io.Serializable;

public class SelectProduct implements Serializable {
    private String name, documentId, price, imgUrl;
    private int quantity;

    public SelectProduct() {
    }

    public SelectProduct(String name, String documentId, String price, String imgUrl, int quantity) {
        this.name = name;
        this.documentId = documentId;
        this.price = price;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}