package com.example.user.grabngo.Admin;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.R;
import com.example.user.grabngo.RecyclerTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class StockOrderFragment extends Fragment {

    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Supplier> supplierList;
    private SupplierAdapter supplierAdapter;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FloatingActionButton fab;
    private Button btnMakeOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stock_order, container, false);

        setHasOptionsMenu(true);
        getActivity().setTitle("Stock Ordering");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycleViewSupplier);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        btnMakeOrder = (Button)v.findViewById(R.id.btn_order);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        supplierList = new ArrayList<>();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Supplier");

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        supplierAdapter = new SupplierAdapter(getActivity(),supplierList);

        btnMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MakeOrderActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Supplier supplier = supplierList.get(position);
                Intent intent = new Intent(getContext(),SupplierDetailActivity.class);
                intent.putExtra("supplierID",supplier.getDocumentId());
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddSupplierActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void show(){
        supplierList.clear();

        mCollectionReference.orderBy("name", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Supplier supplier = documentSnapshot.toObject(Supplier.class);
                    Supplier mSupplier = new Supplier(supplier.getName(), supplier.getEmail(), supplier.getPhone(), supplier.getLocation(), supplier.getPicUrl(), documentSnapshot.getId());
                    supplierList.add(mSupplier);
                }
                supplierAdapter = new SupplierAdapter(getActivity(),supplierList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(supplierAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        supplierList.clear();
        supplierAdapter.notifyDataSetChanged();
        show();
    }

}
