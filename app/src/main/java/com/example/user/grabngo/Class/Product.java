package com.example.user.grabngo.Class;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Product {
    private String productName;
    private String producer;
    private String price;
    private String category;
    private String expired;
    private String shelfLocation;
    private int stockAmount, discount;
    private String imageUrl;
    private String barcode;
    private String modifiedStaffName;
    private @ServerTimestamp Date modifiedDate;
    private String supplierKey;
    private String documentID;
    private boolean lowStockAlert;

    public Product()
    {

    }

    public Product(String barcode, String productName, String producer, String price, String category, String expired, String shelfLocation, int stockAmount, String imageUrl, String modifiedStaffName, Date modifiedDate)
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
        this.supplierKey = supplierKey;
        this.lowStockAlert = true;
        this.discount = discount;
    }

    public Product(String barcode, String productName, String producer, String price, String category, String expired, String shelfLocation, int stockAmount, String imageUrl, Date modifiedDate)
    {
        this.productName = productName;
        this.producer = producer;
        this.price = price;
        this.category = category;
        this.expired = expired;
        this.shelfLocation = shelfLocation;
        this.stockAmount = stockAmount;
        this.imageUrl = imageUrl;
        this.modifiedDate = modifiedDate;
        this.barcode = barcode;
        this.lowStockAlert = true;
    }

    public Product(String imageUrl, String productName, String price)
    {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.price = price;
    }

    public Product(String imageUrl, String productName, int stockAmount, String documentID)
    {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.stockAmount = stockAmount;
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
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
        double temp = Double.parseDouble(price);
        return String.format("%.2f",temp);
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

    public int getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(int stockAmount) {
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

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getSupplierKey() {
        return supplierKey;
    }

    public void setSupplierKey(String supplierKey) {
        this.supplierKey = supplierKey;
    }

    public boolean isLowStockAlert() {
        return lowStockAlert;
    }

    public void setLowStockAlert(boolean lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
