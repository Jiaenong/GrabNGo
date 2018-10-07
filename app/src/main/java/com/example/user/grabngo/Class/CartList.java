package com.example.user.grabngo.Class;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class CartList {
    private static CartList mCartList;
    private List<CartItem> mCartItems;

    public static CartList get(Context context){
        if(mCartList==null){
            mCartList = new CartList(context);
        }
        return mCartList;
    }

    private CartList(Context context){
        mCartItems = new ArrayList<>();
    }

    public void addCartItem(ProductDetail productDetail, int quantity){
        CartItem cartItem = new CartItem(productDetail.getProductName(), productDetail.getImageSrc(),quantity,Double.parseDouble(productDetail.getPrice()));
        mCartItems.add(cartItem);
    }

    public List<CartItem> getCartItems(){
        return mCartItems;
    }

    public CartItem getProduct(String productName){
        for(CartItem cartItem:mCartItems){
            if(cartItem.getProductname().equals(productName)){
                return cartItem;
            }
        }
        return null;
    }

    public double totalPrice(){
        double total = 0;
        for(CartItem cartItem:mCartItems){
            total += cartItem.getPrice()*cartItem.getQuantity();
        }
        return total;
    }
}
