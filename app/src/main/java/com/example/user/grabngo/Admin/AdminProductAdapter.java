package com.example.user.grabngo.Admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;

import java.util.List;


public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;

    @Override
    public AdminProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return new AdminProductAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(AdminProductAdapter.MyViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getProductName());
        holder.stockAmount.setText("Stock Amount: " + product.getStockAmount());
        Glide.with(mContext).load(product.getImageUrl()).into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, stockAmount;
        public ImageView imageViewProduct;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewProductName);
            stockAmount = (TextView)view.findViewById(R.id.textViewProductPrice);
            imageViewProduct = (ImageView)view.findViewById(R.id.imageViewProductImage);
        }
    }

    public AdminProductAdapter(Context mContext, List<Product> productList)
    {
        this.mContext = mContext;
        this.productList = productList;
    }

}
