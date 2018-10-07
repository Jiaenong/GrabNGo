package com.example.user.grabngo.Class;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private static ProductList mProductList;
    private List<ProductDetail> mProducts;

    public static ProductList get(Context context){
        if(mProductList==null){
            mProductList = new ProductList(context);
        }
        return mProductList;
    }

    private ProductList(Context context){
        mProducts = new ArrayList<>();
        mProducts.add(new ProductDetail("988709837649","Cutie Compact Toilet Roll","@drawable/grocery_toiletroll", "15.50", "Cutie Compact","Household","1 year","120","D23-F"));
        mProducts.add(new ProductDetail("1000985247857","Magnum Ice Cream","@drawable/grocery_magnum", "5.00", "Magnum","Food and Beverages","6 months","54","D32-A"));
        mProducts.add(new ProductDetail("1256378998169","Oat Krunch","@drawable/grocery_oatkrunch", "10.80", "Munchy's","Food and Beverages","11 months","256","D32-B"));
        mProducts.add(new ProductDetail("1258976998169","Jongga Spicy Rice Noodle","@drawable/rice_noodle", "37.80", "Chung Jung One","Food and Beverages","1 year","87","D30-C"));
        mProducts.add(new ProductDetail("9636976998167","Listerine Cool Mint","@drawable/listerine", "16.00", "Listerine","Personal Care","9 months","329","D12-A"));
        mProducts.add(new ProductDetail("9636976978121","Colgate Max Fresh toothpaste","@drawable/toothpaste", "8.90", "Colgate","Personal Care","1 year 2 months","69","D12-A"));
        mProducts.add(new ProductDetail("9636788378126","Sunsilk Hair Shampoo","@drawable/sunsilk", "13.90", "Sunsilk","Personal Care","9 months","149","D12-C"));
        mProducts.add(new ProductDetail("6186788378129","Gain Ultra Dish Liquid","@drawable/dish_washing", "6.90", "Procter & Gamble","Household","1 year","329","D22-C"));


    }

    public List<ProductDetail> getProducts() {
        return mProducts;
    }

    public ProductDetail getProduct(String productName){
        for(ProductDetail productDetail:mProducts){
            if(productDetail.getProductName().equals(productName)){
                return productDetail;
            }
        }
        return null;
    }

    public ProductDetail getProductBarcode(String barcode){
        for(ProductDetail productDetail:mProducts){
            if(productDetail.getBarcode().equals(barcode)){
                return productDetail;
            }
        }
        return null;
    }

}
