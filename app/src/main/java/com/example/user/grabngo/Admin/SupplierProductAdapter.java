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
import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.Class.RefundTemp;
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.R;

import java.util.List;


public class SupplierProductAdapter extends RecyclerView.Adapter<SupplierProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;

    @Override
    public SupplierProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_supplier_product,parent,false);
        return new SupplierProductAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(SupplierProductAdapter.MyViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getProductName());
        Glide.with(mContext).load(product.getImageUrl()).into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView imageViewProduct;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewName);
            imageViewProduct = (ImageView)view.findViewById(R.id.imageViewProduct);
        }
    }

    public SupplierProductAdapter(Context mContext, List<Product> productList)
    {
        this.mContext = mContext;
        this.productList = productList;
    }

}
