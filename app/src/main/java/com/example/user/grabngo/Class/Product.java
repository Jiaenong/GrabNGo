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

    public Product()
    {

    }

    public Product(String productName, String producer, String price, String category, String expired, String shelfLocation, String stockAmount, String imageUrl)
    {
        this.productName = productName;
        this.producer = producer;
        this.price = price;
        this.category = category;
        this.expired = expired;
        this.shelfLocation = shelfLocation;
        this.stockAmount = stockAmount;
        this.imageUrl = imageUrl;
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
}
