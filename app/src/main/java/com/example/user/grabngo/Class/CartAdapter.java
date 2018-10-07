package com.example.user.grabngo.Class;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.CartActivity;
import com.example.user.grabngo.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartItem> cart;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product, price, quantity;
        public ImageView productImage;
        public ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            price = (TextView) view.findViewById(R.id.product_price);
            product = (TextView) view.findViewById(R.id.product_name);
            quantity = (TextView) view.findViewById(R.id.product_quantity);
            productImage = (ImageView)view.findViewById(R.id.image_product);
            imageButton = (ImageButton)view.findViewById(R.id.button_delete);
        }
    }

    public CartAdapter(List<CartItem> cartList){
        cart = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CartItem cartItem = cart.get(position);
        holder.product.setText(cartItem.getProductname());
        holder.price.setText(""+cartItem.getPrice());
        holder.quantity.setText(""+cartItem.getQuantity());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }
}
