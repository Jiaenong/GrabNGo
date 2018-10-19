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
import com.example.user.grabngo.R;

import java.util.List;


public class RefundAdapter extends RecyclerView.Adapter<RefundAdapter.MyViewHolder> {
    private Context mContext;
    private List<Refund> refundList;

    @Override
    public RefundAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_refund,parent,false);
        return new RefundAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(RefundAdapter.MyViewHolder holder, int position) {
        Refund refund = refundList.get(position);
        holder.productName.setText(refund.getProductName());
        holder.customerName.setText(refund.getCustomerName());
        holder.day.setText(refund.getDay());
        holder.yearMonth.setText(refund.getMonthYear());
        holder.time.setText(refund.getTime());
    }

    @Override
    public int getItemCount() {
        return refundList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView productName, customerName, day, yearMonth, time;
        public ImageView imageViewProduct;

        public MyViewHolder(View view)
        {
            super(view);
            productName = (TextView)view.findViewById(R.id.textViewProductName);
            customerName = (TextView)view.findViewById(R.id.textViewCustomerName);
            day = (TextView) view.findViewById(R.id.textViewDay);
            yearMonth = (TextView) view.findViewById(R.id.textViewMonthYear);
            time = (TextView) view.findViewById(R.id.textViewTime);
        }
    }

    public RefundAdapter(Context mContext, List<Refund> refundList)
    {
        this.mContext = mContext;
        this.refundList = refundList;
    }

}
