package com.example.user.grabngo;

import android.content.Context;
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
        holder.price.setText(product.getPrice());
        Glide.with(mContext).load(product.getImageUrl()).into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, price;
        public ImageView  imageViewProduct;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewProductName);
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
