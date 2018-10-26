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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.MyViewHolder> {
    private Context mContext;
    private List<Promotion> promotionList;

    @Override
    public PromotionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_promotion,parent,false);
        return new PromotionAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(PromotionAdapter.MyViewHolder holder, int position) {
        Promotion promotion = promotionList.get(position);
        holder.title.setText(promotion.getTitle());
        holder.type.setText("Type       : " + promotion.getType());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.duration.setText("Duration : " + dateFormat.format(promotion.getStartDate()) + " - " + dateFormat.format(promotion.getEndDate()));

        if(promotion.getType().equals("Coupon")){
            holder.imageViewPromotion.setImageResource(R.drawable.discount_coupon);
        }else{
            holder.imageViewPromotion.setImageResource(R.drawable.discount_product);
        }
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, type, duration;
        public ImageView imageViewPromotion;

        public MyViewHolder(View view)
        {
            super(view);
            title = (TextView)view.findViewById(R.id.textViewTitle);
            type = (TextView)view.findViewById(R.id.textViewType);
            duration = (TextView) view.findViewById(R.id.textViewDuration);
            imageViewPromotion = (ImageView)view.findViewById(R.id.imageViewPromotion);
        }
    }

    public PromotionAdapter(Context mContext, List<Promotion> promotionList)
    {
        this.mContext = mContext;
        this.promotionList = promotionList;
    }

}
