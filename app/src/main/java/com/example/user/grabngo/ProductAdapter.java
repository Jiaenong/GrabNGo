package com.example.user.grabngo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);

        return new MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getProductName());
        holder.originalPrice.setVisibility(View.GONE);
        holder.outOfStock.setVisibility(View.GONE);
        if(product.getStockAmount() == 0){
            holder.outOfStock.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.GONE);
            holder.originalPrice.setVisibility(View.GONE);
        }
        if(product.getDiscount()!=0){
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.originalPrice.setText("RM " + product.getPrice());
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            double price = Double.parseDouble(product.getPrice());
            double discountPercent = (100 - product.getDiscount())*0.01;
            price = price * discountPercent;
            holder.price.setText("RM " + String.format("%.2f",price));
        }else{
            holder.price.setText("RM " + product.getPrice());
        }

        Glide.with(mContext).load(product.getImageUrl()).into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, price, originalPrice, outOfStock;
        public ImageView  imageViewProduct;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewProductName);
            originalPrice = (TextView)view.findViewById(R.id.textViewProductOriPrice);
            outOfStock = (TextView)view.findViewById(R.id.textViewOutOfStock);
            price = (TextView)view.findViewById(R.id.textViewProductPrice);
            imageViewProduct = (ImageView)view.findViewById(R.id.imageViewProductImage);
        }
    }

    public ProductAdapter(Context mContext, List<Product> productList)
    {
        this.mContext = mContext;
        this.productList = productList;
    }


}
