package com.example.user.grabngo.Admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IgnoreLowStockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Product> productList;
    private LowStockAdapter lowStockAdapter;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_low_stock);
        IgnoreLowStockActivity.this.setTitle("Manage Low Stock Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycleViewLowStock);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        productList = new ArrayList<>();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        lowStockAdapter = new LowStockAdapter(IgnoreLowStockActivity.this,productList);
    }

    public void show(){
        productList.clear();

        mCollectionReference.whereLessThanOrEqualTo("stockAmount",100).whereEqualTo("lowStockAlert",false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product = documentSnapshot.toObject(Product.class);
                    String image = product.getImageUrl();
                    String name = product.getProductName();
                    int stockAmount = product.getStockAmount();
                    Product mProduct = new Product(image, name, stockAmount, documentSnapshot.getId());
                    productList.add(mProduct);
                }
                lowStockAdapter = new LowStockAdapter(IgnoreLowStockActivity.this,productList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(IgnoreLowStockActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(lowStockAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        productList.clear();
        lowStockAdapter.notifyDataSetChanged();
        show();
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
}
