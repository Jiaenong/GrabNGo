package com.example.user.grabngo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView listViewSearch;
    private RecyclerView recyclerViewSearchResult;
    private ProgressBar progressBarSearch;
    private List<Product> productList, pList;
    SearchView searchView = null;

    private ListViewAdapter adapter;
    private SearchResultAdapter adapter2;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listViewSearch = (ListView)findViewById(R.id.listViewSearch);
        recyclerViewSearchResult = (RecyclerView)findViewById(R.id.recycleViewSearchResult);
        progressBarSearch = (ProgressBar)findViewById(R.id.progressBarSearch);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");
        productList = new ArrayList<>();
        pList = new ArrayList<>();

        progressBarSearch.setVisibility(View.GONE);

        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product = documentSnapshot.toObject(Product.class);
                    String productName = product.getProductName();
                    Product product1 = new Product(productName);
                    productList.add(product1);
                }
                adapter = new ListViewAdapter(SearchActivity.this, productList);
                listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Product product = adapter.getItem(position);
                        Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                        intent.putExtra("productName",product.getProductName());
                        startActivity(intent);
                    }
                });
                listViewSearch.setAdapter(adapter);
            }
        });
        recyclerViewSearchResult.addOnItemTouchListener(new RecyclerTouchListener(SearchActivity.this, recyclerViewSearchResult, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = pList.get(position);
                Intent intent2 = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent2.putExtra("productName", product.getProductName());
                startActivity(intent2);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchManager searchManager = (SearchManager)SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if(searchItem != null)
        {
            searchView = (SearchView)searchItem.getActionView();
        }
        if(searchView != null)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String text = query;
        listViewSearch.setVisibility(View.GONE);
        progressBarSearch.setVisibility(View.VISIBLE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pList.clear();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product = documentSnapshot.toObject(Product.class);
                    /*String name = product.getProductName();
                    String imageUrl = product.getImageUrl();
                    String price = product.getPrice();
                    Product product2 = new Product(imageUrl, name, price);*/
                    pList.add(product);
                }
                adapter2 = new SearchResultAdapter(pList);
                adapter2.searchFilter(text);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewSearchResult.setLayoutManager(mLayoutManager);
                recyclerViewSearchResult.setItemAnimator(new DefaultItemAnimator());
                recyclerViewSearchResult.setAdapter(adapter2);
                progressBarSearch.setVisibility(View.GONE);
                recyclerViewSearchResult.setVisibility(View.VISIBLE);
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

    public class ListViewAdapter extends BaseAdapter{
        Context mContext;
        LayoutInflater inflater;
        private List<Product> listProduct = null;
        private ArrayList<Product> arrayProductList;

        public ListViewAdapter(Context context, List<Product> productList)
        {
            mContext = context;
            this.listProduct = productList;
            inflater = LayoutInflater.from(mContext);
            this.arrayProductList = new ArrayList<Product>();
            this.arrayProductList.addAll(listProduct);
        }

        public class ViewHolder{
            TextView name;
        }
        @Override
        public int getCount() {
            return listProduct.size();
        }

        @Override
        public Product getItem(int position) {
            return listProduct.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.search_view, null);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.name.setText(listProduct.get(position).getProductName());
            return convertView;
        }

        public void filter(String charText){
            charText = charText.toLowerCase(Locale.getDefault());
            listProduct.clear();
            if(charText.length() == 0)
            {
                listProduct.addAll(arrayProductList);
            }else{
                for(Product product : arrayProductList)
                {
                    if(product.getProductName().toLowerCase(Locale.getDefault()).contains(charText)){
                        listProduct.add(product);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>{
        private List<Product> listP;
        private ArrayList<Product> arrayList;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchresult_view, parent, false);
            return new MyViewHolder(resultView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Product product3 = listP.get(position);
            holder.product_name.setText(product3.getProductName());

            holder.ori_product_price.setVisibility(View.GONE);
            if(product3.getDiscount()!=0){
                holder.ori_product_price.setVisibility(View.VISIBLE);
                holder.ori_product_price.setText("RM " + product3.getPrice());
                holder.ori_product_price.setPaintFlags(holder.ori_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                double price = Double.parseDouble(product3.getPrice());
                double discountPercent = (100 - product3.getDiscount())*0.01;
                price = price * discountPercent;
                holder.product_price.setText("RM " + String.format("%.2f",price));
            }else{
                holder.product_price.setText("RM " + product3.getPrice());
            }

            Glide.with(SearchActivity.this).load(product3.getImageUrl()).into(holder.image_product);

        }

        @Override
        public int getItemCount() {
            return listP.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView product_name, product_price, ori_product_price;
            public ImageView image_product;

            public MyViewHolder(View view)
            {
                super(view);
                product_name = (TextView)view.findViewById(R.id.productname);
                product_price = (TextView)view.findViewById(R.id.product_price);
                image_product = (ImageView)view.findViewById(R.id.imageproduct);
                ori_product_price = (TextView)view.findViewById(R.id.textViewProductOriPrice);
            }
        }

        public SearchResultAdapter(List<Product> list)
        {
            listP = list;
            this.arrayList = new ArrayList<Product>();
            this.arrayList.addAll(listP);
        }

        public void searchFilter(String text)
        {
            text = text.toLowerCase(Locale.getDefault());
            listP.clear();
            if(text.length() == 0)
            {
                listP.addAll(arrayList);
            }else{
                for(Product product : arrayList)
                {
                    if(product.getProductName().toLowerCase(Locale.getDefault()).contains(text))
                    {
                        listP.add(product);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}
