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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.CartAdapter;
import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private List<CartItem> cartList;
    private Button btnPayment;
    private RecyclerView mRecyclerView;
    private CartAdapter mAdapter;
    private TextView pricePayment;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private ProgressBar progressBarCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        btnPayment = (Button)findViewById(R.id.btn_payment);
        pricePayment = (TextView)findViewById(R.id.price_payment);
        progressBarCart = (ProgressBar)findViewById(R.id.progressBarCart);
        mRecyclerView = (RecyclerView)findViewById(R.id.cart_recycle_view);
        cartList = new ArrayList<>();
        String id = SaveSharedPreference.getID(CartActivity.this);
        progressBarCart.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");

        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                double totalprice = 0;
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    CartItem cartItem = documentSnapshot.toObject(CartItem.class);
                    String name = cartItem.getProductname();
                    String imageSrc = cartItem.getImageSrc();
                    int qty = cartItem.getQuantity();
                    double price = cartItem.getPrice();
                    CartItem cartitem = new CartItem(name, imageSrc, qty, price);
                    cartList.add(cartitem);
                    totalprice += price*qty;
                }
                mAdapter = new CartAdapter(cartList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                pricePayment.setText("RM " + String.format("%.2f",totalprice));
                progressBarCart.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                if(cartList.isEmpty()){
                    btnPayment.setBackgroundColor(getResources().getColor(R.color.foreground_light_color));

                }else {
                    btnPayment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                            intent.putExtra("totalPrice",pricePayment.getText().toString());
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        private FirebaseFirestore mFirebaseFirestore;
        private CollectionReference mCollectionReference;
        private DocumentReference mDocumentReference;

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
            mFirebaseFirestore = FirebaseFirestore.getInstance();
            final CartItem cartItem = cart.get(position);
            holder.product.setText(cartItem.getProductname());
            holder.price.setText("RM "+String.format("%.2f",cartItem.getPrice()));
            holder.quantity.setText("Quantity: "+cartItem.getQuantity());
            Glide.with(CartActivity.this).load(cartItem.getImageSrc()).into(holder.productImage);

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = cartItem.getProductname();
                    String id = SaveSharedPreference.getID(CartActivity.this);
                    Log.i("Testing :",name);
                    mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");
                    mCollectionReference.whereEqualTo("productname",name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            double price = 0, totalPrice = 0;
                            String key = "";
                            String id = SaveSharedPreference.getID(CartActivity.this);
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                CartItem cart = documentSnapshot.toObject(CartItem.class);
                                key = documentSnapshot.getId();
                                price = cart.getPrice();

                            }
                            mDocumentReference = mFirebaseFirestore.document("Customer/"+id+"/Cart/"+key);
                            mDocumentReference.delete();
                            String text = pricePayment.getText().toString();
                            totalPrice = Double.parseDouble(text.substring(2));
                            totalPrice -= price;
                            pricePayment.setText("RM " + totalPrice);

                            if(cartList.isEmpty()){
                                btnPayment.setBackgroundColor(getResources().getColor(R.color.foreground_light_color));
                                btnPayment.setOnClickListener(null);
                            }
                        }
                    });

                    cart.remove(position);
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
