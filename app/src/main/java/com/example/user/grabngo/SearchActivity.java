package com.example.user.grabngo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private List<Product> productList;
    SearchView searchView = null;

    private ListViewAdapter adapter;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listViewSearch = (ListView)findViewById(R.id.listViewSearch);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");
        productList = new ArrayList<>();

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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
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
}
