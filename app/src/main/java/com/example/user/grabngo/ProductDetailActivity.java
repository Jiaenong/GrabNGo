package com.example.user.grabngo;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.user.grabngo.Class.ProductDetail;
import com.example.user.grabngo.Class.ProductList;

import org.w3c.dom.Text;

public class ProductDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewName, textViewPrice, textViewProducer, textViewCategory, textViewExpired, textViewStock, textViewLocation;
    private ImageView imageViewProduct;

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

        ProductList productList = ProductList.get(ProductDetailActivity.this);

        String productName = getIntent().getExtras().getString("pName");
        ProductDetail product = productList.getProduct(productName);

        textViewName.setText(product.getProductName());
        textViewPrice.setText("RM " + product.getPrice());
        textViewProducer.setText("Producer               : " + product.getProducer());
        textViewCategory.setText("Category               : " + product.getCategory());
        textViewExpired.setText("Expired Duration : " + product.getExpiredDuration());
        textViewStock.setText("Stock Amount     : " + product.getStock());
        textViewLocation.setText("Shelf location      : " + product.getLocation());

        int imageResource = getResources().getIdentifier(product.getImageSrc(), null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        imageViewProduct.setImageDrawable(res);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
