package com.example.user.grabngo;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Admin.StaffProductDetailActivity;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.ProductDetail;
import com.example.user.grabngo.Class.ProductList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class ProductDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewName, textViewPrice, textViewProducer, textViewCategory, textViewExpired, textViewStock, textViewLocation;
    private ImageView imageViewProduct;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private DocumentReference mDocumentReference;
    private ProgressBar progressBarProductDetail;
    private CardView cardViewProduct1, cardViewProduct2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        textViewName = (TextView)findViewById(R.id.text_view_product_name);
        textViewPrice = (TextView)findViewById(R.id.text_view_product_price);
        textViewProducer = (TextView)findViewById(R.id.text_view_producer);
        textViewCategory = (TextView)findViewById(R.id.text_view_category);
        textViewExpired = (TextView)findViewById(R.id.text_view_expired);
        textViewStock = (TextView)findViewById(R.id.text_view_stock);
        textViewLocation = (TextView)findViewById(R.id.text_view_location);
        imageViewProduct = (ImageView)findViewById(R.id.product_image);
        progressBarProductDetail = (ProgressBar)findViewById(R.id.progressBarProductDetail);
        cardViewProduct1 = (CardView)findViewById(R.id.cardViewProduct1);
        cardViewProduct2 = (CardView)findViewById(R.id.cardViewProduct2);

        cardViewProduct1.setVisibility(View.GONE);
        cardViewProduct2.setVisibility(View.GONE);
        progressBarProductDetail.setVisibility(View.VISIBLE);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        Intent intent = getIntent();
        String name = intent.getStringExtra("productName");
        mCollectionReference.whereEqualTo("productName",name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    String key = documentSnapshot.getId();
                    showProductDetail(key);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void showProductDetail(String key) {
        mDocumentReference = mFirebaseFirestore.document("Product/"+key);
        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                textViewName.setText(product.getProductName());
                textViewPrice.setText("RM " + product.getPrice());
                textViewProducer.setText("Producer               : " + product.getProducer());
                textViewCategory.setText("Category               : " + product.getCategory());
                textViewExpired.setText("Expired Date        : " + product.getExpired());
                textViewStock.setText("Stock Amount     : " + product.getStockAmount());
                textViewLocation.setText("Shelf location      : " + product.getShelfLocation());
                Glide.with(ProductDetailActivity.this).load(product.getImageUrl()).into(imageViewProduct);
                cardViewProduct1.setVisibility(View.VISIBLE);
                cardViewProduct2.setVisibility(View.VISIBLE);
                progressBarProductDetail.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.cart:
                startActivity(new Intent(ProductDetailActivity.this,CartActivity.class));
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
