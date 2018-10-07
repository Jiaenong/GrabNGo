package com.example.user.grabngo.Admin;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.grabngo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffAcountFragment extends Fragment {

    private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_staff_acount, container, false);

        getActivity().setTitle("Account");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);

        return v;
    }

}
