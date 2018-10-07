package com.example.user.grabngo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.CartAdapter;
import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    CartList mCartList = CartList.get(CartActivity.this);
    private List<CartItem> cartList = mCartList.getCartItems();
    private Button btnPayment;
    private RecyclerView mRecyclerView;
    private  CartAdapter mAdapter;
    private TextView pricePayment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mRecyclerView = (RecyclerView)findViewById(R.id.cart_recycle_view);
        mAdapter = new CartAdapter(cartList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPayment = (Button)findViewById(R.id.btn_payment);
        pricePayment = (TextView)findViewById(R.id.price_payment);
        pricePayment.setText("RM " + String.format("%.2f",mCartList.totalPrice()));

        if(cartList.isEmpty()){
            btnPayment.setBackgroundColor(getResources().getColor(R.color.foreground_light_color));

        }else {
            btnPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

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
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            CartItem cartItem = cart.get(position);
            holder.product.setText(cartItem.getProductname());
            holder.price.setText("RM "+String.format("%.2f",cartItem.getPrice()));
            holder.quantity.setText("Quantity: "+cartItem.getQuantity());

            int imageResource = getResources().getIdentifier(cartItem.getImageSrc(), null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            holder.productImage.setImageDrawable(res);

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cart.remove(position);
                    if(cartList.isEmpty()){
                        btnPayment.setBackgroundColor(getResources().getColor(R.color.foreground_light_color));
                        btnPayment.setOnClickListener(null);
                    }

                    pricePayment.setText("RM " + String.format("%.2f",mCartList.totalPrice()));
                    Toast.makeText(CartActivity.this, "Item removed from cart",Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,cart.size());
                }
            });
        }

        @Override
        public int getItemCount() {
            return cart.size();
        }
    }


}
