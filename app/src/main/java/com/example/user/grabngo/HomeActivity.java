package com.example.user.grabngo;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.widget.Toast;

import com.example.user.grabngo.Admin.ForumFragment;

import java.lang.reflect.Field;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Fragment fragment = new HomeFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
                        return true;
                    case R.id.navigation_products:
                        Fragment fragment1 = new ProductsFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,fragment1).commit();
                        return true;
                    case R.id.navigation_scan:
                        Intent intent = new Intent(HomeActivity.this,ScanBarcodeActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_qna:
                        Fragment fragment4 = new ForumFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,fragment4).commit();
                        return true;
                    case R.id.navigation_account:
                        Fragment fragment3 = new AccountFragment();
                        fm.beginTransaction().replace(R.id.fragment_container,fragment3).commit();
                        return true;
                }
                return false;
            }
        };

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = new HomeFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }



}
