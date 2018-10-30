package com.example.user.grabngo.Admin;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.grabngo.CartActivity;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.HomeActivity;
import com.example.user.grabngo.ProductAdapter;
import com.example.user.grabngo.ProductDetailActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.RecyclerTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {

    private Spinner spinnerCategory, spinnerSortby;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private AdminProductAdapter adminProductAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        setHasOptionsMenu(true);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycleViewProduct);
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        productList = new ArrayList<>();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");
        Task<QuerySnapshot> sp = mCollectionReference.orderBy("productName",Query.Direction.DESCENDING).get();
        Query query = mCollectionReference.orderBy("productName");

        getActivity().setTitle("Inventory");
        navigationView.getMenu().getItem(1).setChecked(true);

        spinnerCategory = (Spinner)v.findViewById(R.id.spinner_category);
        spinnerSortby = (Spinner)v.findViewById(R.id.spinner_sortby);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity() ,R.array.sortBy, R.layout.support_simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerSortby.setAdapter(adapter2);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddProductActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(getContext(),StaffProductDetailActivity.class);
                intent.putExtra("productID",product.getDocumentID());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        spinnerSortby.setSelection(spinnerSortby.getSelectedItemPosition(),false);
        spinnerSortby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                filterProduct();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCategory.setSelection(spinnerCategory.getSelectedItemPosition(),false);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                filterProduct();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adminProductAdapter = new AdminProductAdapter(getActivity(),productList);

        return v;
    }

    public void filterProduct(){
        productList.clear();
        int sortBy = spinnerSortby.getSelectedItemPosition();
        Query query;

        if(spinnerCategory.getSelectedItemPosition() == 0){
            if(sortBy == 0)
                query = mCollectionReference.orderBy("modifiedDate",Query.Direction.DESCENDING);
            else if(sortBy == 1)
                query = mCollectionReference.orderBy("modifiedDate",Query.Direction.ASCENDING);
            else if(sortBy == 2)
                query = mCollectionReference.orderBy("productName",Query.Direction.ASCENDING);
            else
                query = mCollectionReference.orderBy("stockAmount",Query.Direction.ASCENDING);

        }else{
            if(sortBy == 0)
                query = mCollectionReference.whereEqualTo("category",spinnerCategory.getSelectedItem().toString()).orderBy("modifiedDate",Query.Direction.DESCENDING);
            else if(sortBy == 1)
                query = mCollectionReference.whereEqualTo("category",spinnerCategory.getSelectedItem().toString()).orderBy("modifiedDate",Query.Direction.ASCENDING);
            else if(sortBy == 2)
                query = mCollectionReference.whereEqualTo("category",spinnerCategory.getSelectedItem().toString()).orderBy("productName",Query.Direction.ASCENDING);
            else
                query = mCollectionReference.whereEqualTo("category",spinnerCategory.getSelectedItem().toString()).orderBy("stockAmount",Query.Direction.ASCENDING);

        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                adminProductAdapter = new AdminProductAdapter(getActivity(),productList);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adminProductAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                Toast.makeText(getActivity(),"GOOD",Toast.LENGTH_SHORT).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        productList.clear();
        adminProductAdapter.notifyDataSetChanged();
        filterProduct();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inventory_menu,menu);
        MenuItem item = menu.findItem(R.id.report);
        item.setVisible(false);
        return;
    }


}
