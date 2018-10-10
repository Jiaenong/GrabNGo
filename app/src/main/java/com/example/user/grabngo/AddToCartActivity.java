package com.example.user.grabngo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.ProductDetail;
import com.example.user.grabngo.Class.ProductList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddToCartActivity extends AppCompatActivity {

    private Button btnAddToCart;
    private ImageButton btn_add, btn_minus;
    private EditText quantity;
    private ImageView imageView;
    private TextView textViewPrice, textViewProduct, textViewProducer, textViewCategory, textViewExpired, textViewStock, textViewLocation;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        Intent intent = getIntent();
        String productKey = intent.getStringExtra("productID");

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Product/"+productKey);

        textViewPrice = (TextView)findViewById(R.id.text_view_price);
        textViewProduct = (TextView)findViewById(R.id.text_view_product_name);
        textViewProducer = (TextView)findViewById(R.id.text_view_producer);
        textViewCategory = (TextView)findViewById(R.id.text_view_category);
        textViewExpired = (TextView)findViewById(R.id.text_view_expired);
        textViewStock = (TextView)findViewById(R.id.text_view_stock);
        textViewLocation = (TextView)findViewById(R.id.text_view_location);
        imageView = (ImageView)findViewById(R.id.product_image);
        quantity = (EditText)findViewById(R.id.edit_text_quantity);
        btn_add = (ImageButton)findViewById(R.id.button_plus);
        btn_minus = (ImageButton)findViewById(R.id.button_minus);
        progressBar = (ProgressBar)findViewById(R.id.progressBarAddToCart);
        scrollView = (ScrollView)findViewById(R.id.scrollview);

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mQuantity = Integer.parseInt(quantity.getText().toString());
                mQuantity++;
                quantity.setText(""+mQuantity);
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mQuantity = Integer.parseInt(quantity.getText().toString());
                if(mQuantity==1){
                    return;
                }
                mQuantity--;
                quantity.setText(""+mQuantity);
            }
        });

        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                Glide.with(AddToCartActivity.this).load(product.getImageUrl()).into(imageView);
                textViewProduct.setText(product.getProductName());
                textViewPrice.setText("RM " + product.getPrice());
                textViewProducer.setText("Producer               : " + product.getProducer());
                textViewCategory.setText("Category               : " + product.getCategory());
                textViewExpired.setText("Expired Date        : " + product.getExpired());
                textViewStock.setText("Stock Amount     : " + product.getStockAmount());
                textViewLocation.setText("Shelf location      : " + product.getShelfLocation());
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

            }
        });

        btnAddToCart = (Button)findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartList cartList = CartList.get(AddToCartActivity.this);

                int i = Integer.parseInt(quantity.getText().toString());
                //cartList.addCartItem(product,i);

                finish();
                Toast.makeText(AddToCartActivity.this, "Item successfully added to cart",Toast.LENGTH_SHORT).show();
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
            case R.id.cart:
                startActivity(new Intent(AddToCartActivity.this,CartActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return true;
    }
}
