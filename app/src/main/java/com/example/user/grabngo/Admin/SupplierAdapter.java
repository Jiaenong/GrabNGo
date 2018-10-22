package com.example.user.grabngo.Admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.Class.RefundTemp;
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.R;

import java.util.List;


public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {
    private Context mContext;
    private List<Supplier> supplierList;

    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_supplier,parent,false);
        return new SupplierAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(SupplierAdapter.MyViewHolder holder, int position) {
        Supplier supplier = supplierList.get(position);
        holder.name.setText(supplier.getName());
        holder.email.setText("Email : " + supplier.getEmail());
        holder.phone.setText("H/P    : " + supplier.getPhone());
        Glide.with(mContext).load(supplier.getPicUrl()).into(holder.imageViewSupplier);
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, email, phone;
        public ImageView imageViewSupplier;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewName);
            email = (TextView)view.findViewById(R.id.textViewEmail);
            phone = (TextView) view.findViewById(R.id.textViewPhone);
            imageViewSupplier = (ImageView)view.findViewById(R.id.imageViewSupplier);
        }
    }

    public SupplierAdapter(Context mContext, List<Supplier> supplierList)
    {
        this.mContext = mContext;
        this.supplierList = supplierList;
    }

}
