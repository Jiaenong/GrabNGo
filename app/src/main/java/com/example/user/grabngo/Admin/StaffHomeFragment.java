package com.example.user.grabngo.Admin;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.user.grabngo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffHomeFragment extends Fragment {

    private CardView cardViewInventory, cardViewRefund, cardViewForum, cardViewAccount;
    private NavigationView navigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_staff_home, container, false);

        cardViewAccount = (CardView)v.findViewById(R.id.cardview_account);
        cardViewInventory = (CardView)v.findViewById(R.id.cardview_inventory);
        cardViewRefund = (CardView)v.findViewById(R.id.cardview_refund);
        cardViewForum = (CardView)v.findViewById(R.id.cardview_qnaforum);

        getActivity().setTitle("Home");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        final FragmentManager fm = getFragmentManager();

        cardViewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new InventoryFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        cardViewRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RefundFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        cardViewForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ForumFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        cardViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new StaffAcountFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        return v;
    }

}
