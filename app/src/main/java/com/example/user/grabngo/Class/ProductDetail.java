package com.example.user.grabngo.Class;

public class ProductDetail {

    private String barcode;
    private String productName;
    private String imageSrc;
    private String price;
    private String producer;
    private String category;
    private String expiredDuration;
    private String stock;
    private String location;

    public ProductDetail(String barcode, String productName, String imageSrc, String price, String producer, String category, String expiredDuration, String stock, String location) {
        this.barcode = barcode;
        this.productName = productName;
        this.imageSrc = imageSrc;
        this.price = price;
        this.producer = producer;
        this.category = category;
        this.expiredDuration = expiredDuration;
        this.stock = stock;
        this.location = location;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpiredDuration() {
        return expiredDuration;
    }

    public void setExpiredDuration(String expiredDuration) {
        this.expiredDuration = expiredDuration;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
