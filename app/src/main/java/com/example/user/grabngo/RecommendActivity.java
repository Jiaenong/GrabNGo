package com.example.user.grabngo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.grabngo.Class.Product;

import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRecommendMore;
    private List<Product> moreList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Intent intent = getIntent();
        moreList = (List<Product>)intent.getSerializableExtra("myList");
        if(intent.getStringExtra("check").equals("Recommend"))
        {
            setTitle("Recommend");
        }else{
            setTitle("New Arrival");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewRecommendMore = (RecyclerView)findViewById(R.id.recycleViewRecommendMore);
        adapter = new ProductAdapter(RecommendActivity.this,moreList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerViewRecommendMore.setLayoutManager(mLayoutManager);
        recyclerViewRecommendMore.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRecommendMore.setAdapter(adapter);

        recyclerViewRecommendMore.addOnItemTouchListener(new RecyclerTouchListener(RecommendActivity.this, recyclerViewRecommendMore, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = moreList.get(position);
                Intent intent1 = new Intent(RecommendActivity.this, ProductDetailActivity.class);
                intent1.putExtra("productName",product.getProductName());
                startActivity(intent1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
