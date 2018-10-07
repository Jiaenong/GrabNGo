package com.example.user.grabngo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class ProductsFragment extends Fragment {

    private Spinner spinnerCategory;
    private LinearLayout productLayout,productLayout2,productLayout3,productLayout4,productLayout5,productLayout6,productLayout7,productLayout8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_products);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_products, container, false);

        setHasOptionsMenu(true);
        productLayout = (LinearLayout)v.findViewById(R.id.layout_product);
        productLayout2 = (LinearLayout)v.findViewById(R.id.layout_product2);
        productLayout3 = (LinearLayout)v.findViewById(R.id.layout_product3);
        productLayout4 = (LinearLayout)v.findViewById(R.id.layout_product4);
        productLayout5 = (LinearLayout)v.findViewById(R.id.layout_product5);
        productLayout6 = (LinearLayout)v.findViewById(R.id.layout_product6);
        productLayout7 = (LinearLayout)v.findViewById(R.id.layout_product7);
        productLayout8 = (LinearLayout)v.findViewById(R.id.layout_product8);
        spinnerCategory = (Spinner)v.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.category, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = adapterView.getItemAtPosition(i).toString();
                FragmentManager fm = getFragmentManager();
                switch (selectedItem){
                    case "All":
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

        productLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Jongga Spicy Rice Noodle";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Listerine Cool Mint";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Colgate Max Fresh toothpaste";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = "Sunsilk Hair Shampoo";
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("pName",productName);
                startActivity(intent);
            }
        });

        productLayout8.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(getActivity(),CartActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart,menu);
        return;
    }

}
