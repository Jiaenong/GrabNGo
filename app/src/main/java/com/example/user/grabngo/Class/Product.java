package com.example.user.grabngo.Class;

public class Product {
    private String productName;
    private String producer;
    private String price;
    private String category;
    private String expired;
    private String shelfLocation;
    private String stockAmount;
    private String imageUrl;
    private String barcode;
    private String modifiedStaffName;
    private String modifiedDate;
    private String modifiedTime;
    private String supplierKey;
    private String staffKey;

    public Product()
    {

    }

    public Product(String barcode, String productName, String producer, String price, String category, String expired, String shelfLocation, String stockAmount, String imageUrl, String modifiedStaffName, String modifiedDate, String modifiedTime, String supplierKey, String staffKey)
    {
        this.productName = productName;
        this.producer = producer;
        this.price = price;
        this.category = category;
        this.expired = expired;
        this.shelfLocation = shelfLocation;
        this.stockAmount = stockAmount;
        this.imageUrl = imageUrl;
        this.barcode = barcode;
        this.modifiedStaffName = modifiedStaffName;
        this.modifiedDate = modifiedDate;
        this.modifiedTime = modifiedTime;
        this.supplierKey = supplierKey;
        this.staffKey = staffKey;
    }

    public Product(String barcode, String productName, String producer, String price, String category, String expired, String shelfLocation, String stockAmount, String imageUrl)
    {
        this.productName = productName;
        this.producer = producer;
        this.price = price;
        this.category = category;
        this.expired = expired;
        this.shelfLocation = shelfLocation;
        this.stockAmount = stockAmount;
        this.imageUrl = imageUrl;
        this.barcode = barcode;
    }

    public Product(String imageUrl, String productName, String price)
    {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(String stockAmount) {
        this.stockAmount = stockAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getModifiedStaffName() {
        return modifiedStaffName;
    }

    public void setModifiedStaffName(String modifiedStaffName) {
        this.modifiedStaffName = modifiedStaffName;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getSupplierKey() {
        return supplierKey;
    }

    public void setSupplierKey(String supplierKey) {
        this.supplierKey = supplierKey;
    }

    public String getStaffKey() {
        return staffKey;
    }

    public void setStaffKey(String staffKey) {
        this.staffKey = staffKey;
    }
}
