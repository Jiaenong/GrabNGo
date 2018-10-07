package com.example.user.grabngo.Admin;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.grabngo.R;
import com.example.user.grabngo.ViewDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerHomeFragment extends Fragment {

    private NavigationView navigationView;
    private CardView cardViewLowStockAlert, cardViewLowStock, cardViewStockOrdering, cardViewPromotion, cardViewReport;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manager_home, container, false);

        getActivity().setTitle("Home");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        cardViewLowStockAlert = (CardView)v.findViewById(R.id.low_stock);
        cardViewLowStockAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(getActivity());
            }
        });

        cardViewLowStock = (CardView)v.findViewById(R.id.cardview_low_stock);
        cardViewStockOrdering = (CardView)v.findViewById(R.id.cardview_stock_order);
        cardViewPromotion = (CardView)v.findViewById(R.id.cardview_promotion);
        cardViewReport = (CardView)v.findViewById(R.id.cardview_report);

        final FragmentManager fm = getFragmentManager();

        cardViewLowStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LowStockFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewStockOrdering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new StockOrderFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ReportFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PromotionFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        return v;
    }

}
