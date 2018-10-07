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
public class FoodFragment extends Fragment {

    private Spinner spinnerCategory;
    private LinearLayout productLayout,productLayout2,productLayout3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_food, container, false);

        productLayout = (LinearLayout)v.findViewById(R.id.food_product1);
        productLayout2 = (LinearLayout)v.findViewById(R.id.food_product2);
        productLayout3 = (LinearLayout)v.findViewById(R.id.food_product3);
        spinnerCategory = (Spinner)v.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setSelection(3);
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
                        Fragment householdFragment = new HouseholdFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,householdFragment).commit();
                        break;
                    case "Food and Beverages":
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
                String productName = "Jongga Spicy Rice Noodle";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Magnum Ice Cream";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Oat Krunch";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });


        return v;
    }

}
