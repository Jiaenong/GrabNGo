package com.example.user.grabngo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Cart;
import com.example.user.grabngo.Class.CartAdapter;
import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;
import com.example.user.grabngo.Class.Comment;
import com.example.user.grabngo.Class.Coupon;
import com.example.user.grabngo.Class.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private List<CartItem> cartList;
    private Button btnPayment, btnApplyPromo, btnChange;
    private EditText editTextPromo;
    private RecyclerView mRecyclerView;
    private CartAdapter mAdapter;
    private TextView pricePayment, textViewPromo, textViewAppliedPromo;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private ProgressBar progressBarCart;
    private int promoPrice;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        editTextPromo = (EditText)findViewById(R.id.editTextPromo);
        textViewPromo = (TextView)findViewById(R.id.textViewPromo);
        textViewAppliedPromo = (TextView)findViewById(R.id.textViewAppliedPromo);
        btnPayment = (Button)findViewById(R.id.btn_payment);
        btnChange = (Button)findViewById(R.id.btn_change);
        btnApplyPromo = (Button)findViewById(R.id.btn_apply_promo);
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
                totalPrice = 0;

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Cart cart = documentSnapshot.toObject(Cart.class);
                    cart.setCartRef(documentSnapshot.getId().toString());
                    String productRef = cart.getProductRef();

                    mFirebaseFirestore.collection("Product").document(productRef).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot1) {
                            Product product = documentSnapshot1.toObject(Product.class);

                            double price = Double.parseDouble(product.getPrice());
                            if(product.getDiscount()!=0){
                                double discountPercent = (100 - product.getDiscount())*0.01;
                                price = price * discountPercent;
                            }

                            CartItem cartItem = new CartItem(product.getProductName(),
                                    product.getImageUrl(),
                                    cart.getQuantity(),
                                    price,
                                    cart.getCartRef());

                            cartList.add(cartItem);
                            totalPrice += price*cart.getQuantity();
                            mAdapter = new CartAdapter(cartList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mRecyclerView.setAdapter(mAdapter);
                            pricePayment.setText("RM " + String.format("%.2f",totalPrice));
                            Toast.makeText(CartActivity.this, ""+cartList.size(), Toast.LENGTH_SHORT).show();

                            if(!cartList.isEmpty()){
                                editTextPromo.setFocusableInTouchMode(true);
                                editTextPromo.setFocusable(true);
                                btnPayment.setBackgroundColor(getResources().getColor(R.color.price_color));
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
                }


                progressBarCart.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

            }
        });

        btnApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPromo.getText().toString().equals("")){
                    return;
                }

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                List<Coupon> couponList = new ArrayList<>();
                String promoCode = editTextPromo.getText().toString();
                mFirebaseFirestore.collection("Promotion").whereEqualTo("type","Coupon").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean valid = false;
                        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            Coupon coupon = documentSnapshot.toObject(Coupon.class);
                            couponList.add(coupon);
                        }

                        //TODO: TEST NULL
                        if(couponList!=null) {
                            for (int i = 0; i < couponList.size(); i++) {
                                if (couponList.get(i).getCode().equals(promoCode)) {
                                    valid = true;

                                    double totalPrice = Double.parseDouble(pricePayment.getText().toString().substring(2));
                                    double rebatePrice = totalPrice - (double)couponList.get(i).getCashRebate();

                                    if(rebatePrice<=0){
                                        Toast.makeText(CartActivity.this, "Only applicable with a purchase of more than RM " + couponList.get(i).getCashRebate(), Toast.LENGTH_LONG).show();
                                    }else{

                                        btnApplyPromo.setVisibility(View.GONE);
                                        editTextPromo.setVisibility(View.GONE);
                                        btnChange.setVisibility(View.VISIBLE);
                                        textViewAppliedPromo.setVisibility(View.VISIBLE);
                                        promoPrice = couponList.get(i).getCashRebate();
                                        textViewAppliedPromo.setText("Applied Promo Code : " + promoCode);
                                        textViewPromo.setText("Promo Saved: RM " + String.format("%.2f",(double)couponList.get(i).getCashRebate()));
                                        pricePayment.setText("RM " + String.format("%.2f", rebatePrice));
                                        Toast.makeText(CartActivity.this, "Promo code successfully applied", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                                }
                            }
                        }

                        if(!valid){
                            Toast.makeText(CartActivity.this,"Invalid promo code",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnApplyPromo.setVisibility(View.VISIBLE);
                editTextPromo.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                textViewAppliedPromo.setVisibility(View.GONE);
                editTextPromo.setText("");
                textViewPromo.setText("Promo Saved: RM 0.00");
                double totalPrice = Double.parseDouble(pricePayment.getText().toString().substring(2));
                double originalPrice = totalPrice+promoPrice;
                promoPrice = 0;
                pricePayment.setText("RM " + String.format("%.2f", originalPrice));
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

                    String id = SaveSharedPreference.getID(CartActivity.this);
                    mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");

                    totalPrice = Double.parseDouble(pricePayment.getText().toString().substring(2));
                    totalPrice -= cartItem.getPrice()*cartItem.getQuantity();
                    if(totalPrice<=promoPrice && promoPrice!=0){

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CartActivity.this, R.style.AlertDialogCustom));
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                btnApplyPromo.setVisibility(View.VISIBLE);
                                editTextPromo.setVisibility(View.VISIBLE);
                                btnChange.setVisibility(View.GONE);
                                textViewAppliedPromo.setVisibility(View.GONE);
                                editTextPromo.setText("");
                                textViewPromo.setText("Promo Saved: RM 0.00");
                                totalPrice += promoPrice;
                                promoPrice =0;
                                pricePayment.setText("RM " + String.format("%.2f", totalPrice));
                                updateCart(position,cartItem);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                        builder.setTitle("Warning");
                        builder.setMessage("Promo only applicable with minimum purchase of RM " + promoPrice + ". Continue to delete?");
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else {
                        updateCart(position,cartItem);
                    }



                }
            });
        }

        public void updateCart(int position, CartItem cartItem){
            pricePayment.setText("RM " + String.format("%.2f",totalPrice));

            cart.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,cart.size());

            if(cartList.isEmpty()){
                editTextPromo.setFocusable(false);
                btnApplyPromo.setOnClickListener(null);
                btnPayment.setBackgroundColor(getResources().getColor(R.color.foreground_light_color));
                btnPayment.setOnClickListener(null); }

            mCollectionReference.document(cartItem.getProductKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    return;
                }
            });
        }

        @Override
        public int getItemCount() {
            return cart.size();
        }
    }


}
