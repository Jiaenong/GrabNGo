package com.example.user.grabngo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HouseholdFragment extends Fragment {

    private Spinner spinnerCategory;
    private LinearLayout productLayout, productLayout2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_household, container, false);

        productLayout = (LinearLayout)v.findViewById(R.id.household_product1);
        productLayout2 = (LinearLayout)v.findViewById(R.id.household_product2);
        spinnerCategory = (Spinner)v.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setSelection(2);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = adapterView.getItemAtPosition(i).toString();
                FragmentManager fm = getFragmentManager();
                switch (selectedItem){
                    case "All":
                        Fragment allFragment = new ProductsFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,allFragment).commit();
                        break;
                    case "Personal Care":
                        Fragment personalCareFragment = new PersonalCareFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,personalCareFragment).commit();
                        break;
                    case "Household":
                        break;
                    case "Food and Beverages":
                        Fragment foodFragment = new FoodFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,foodFragment).commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        productLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Cutie Compact Toilet Roll";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Gain Ultra Dish Liquid";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });


        return v;
    }

}
