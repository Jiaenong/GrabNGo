package com.example.user.grabngo.Admin;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.user.grabngo.Class.LowStockNotification;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LowStockFragment extends Fragment {

    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Product> productList;
    private LowStockAdapter lowStockAdapter;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_low_stock, container, false);
        setHasOptionsMenu(true);

        getActivity().setTitle("Low Stock Product");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycleViewLowStock);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        productList = new ArrayList<>();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        lowStockAdapter = new LowStockAdapter(getActivity(),productList);

        return v;
    }

    public void show(){
        productList.clear();

        mCollectionReference.whereLessThanOrEqualTo("stockAmount",100).whereEqualTo("lowStockAlert",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                lowStockAdapter = new LowStockAdapter(getActivity(),productList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
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
            case R.id.action_ignore_item:
                Intent intent = new Intent(getActivity(),IgnoreLowStockActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.low_stock,menu);
        return;
    }

}
