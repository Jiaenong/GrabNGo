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
public class PersonalCareFragment extends Fragment {

    private Spinner spinnerCategory;
    private LinearLayout productLayout,productLayout2,productLayout3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_personal_care, container, false);

        productLayout = (LinearLayout)v.findViewById(R.id.care_product1);
        productLayout2 = (LinearLayout)v.findViewById(R.id.care_product2);
        productLayout3 = (LinearLayout)v.findViewById(R.id.care_product3);
        spinnerCategory = (Spinner)v.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setSelection(1);
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
                        break;
                    case "Household":
                        Fragment householdFragment = new HouseholdFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,householdFragment).commit();
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
                String productName = "Colgate Max Fresh toothpaste";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Listerine Cool Mint";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Sunsilk Hair Shampoo";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        return v;
    }

}
