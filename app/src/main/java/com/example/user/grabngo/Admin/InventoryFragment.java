package com.example.user.grabngo.Admin;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.grabngo.CartActivity;
import com.example.user.grabngo.HomeActivity;
import com.example.user.grabngo.ProductDetailActivity;
import com.example.user.grabngo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {

    private Spinner spinnerCategory, spinnerSortby;
    private NavigationView navigationView;
    private LinearLayout productLayout4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        setHasOptionsMenu(true);
        productLayout4 = (LinearLayout)v.findViewById(R.id.layout_product);
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);

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

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        productLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Jongga Spicy Rice Noodle";
                Intent intent = new Intent(getActivity(), StaffProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        return v;
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inventory_menu,menu);
        return;
    }

}
