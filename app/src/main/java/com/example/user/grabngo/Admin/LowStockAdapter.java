package com.example.user.grabngo.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;

    @Override
    public LowStockAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = null;

        if(mContext instanceof IgnoreLowStockActivity){
            productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_ignoreproduct,parent,false);
        }else {
            productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lowstock,parent,false);
        }

        return new LowStockAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(LowStockAdapter.MyViewHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.name.setText(product.getProductName());
        holder.stockAmount.setText("Stock Amount: " + product.getStockAmount());
        Glide.with(mContext).load(product.getImageUrl()).into(holder.imageViewProduct);
        holder.btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MakeOrderActivity.class);
                intent.putExtra("productName",product.getProductName());
                mContext.startActivity(intent);
            }
        });

        if(mContext instanceof IgnoreLowStockActivity) {
            holder.btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore.getInstance().collection("Product").document(product.getDocumentID()).update("lowStockAlert", true);
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                }
            });

        }else{
            holder.btnIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore.getInstance().collection("Product").document(product.getDocumentID()).update("lowStockAlert", false);
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, stockAmount;
        public ImageView imageViewProduct;
        public Button btnIgnore, btnReorder,btnRestore;


        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewProductName);
            stockAmount = (TextView)view.findViewById(R.id.textViewProductQuantity);
            imageViewProduct = (ImageView)view.findViewById(R.id.imageViewProductImage);

            if(mContext instanceof IgnoreLowStockActivity){
                btnRestore = (Button)view.findViewById(R.id.btn_restore);
            }else {
                btnIgnore = (Button) view.findViewById(R.id.btn_ignore);
                btnReorder = (Button) view.findViewById(R.id.btn_reorder);
            }

        }
    }

    public LowStockAdapter(Context mContext, List<Product> productList)
    {
        this.mContext = mContext;
        this.productList = productList;
    }

}
