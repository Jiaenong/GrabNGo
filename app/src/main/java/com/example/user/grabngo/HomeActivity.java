package com.example.user.grabngo;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Field;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    public String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent2 = getIntent();
        tag = intent2.getStringExtra("tag");
        String id = SaveSharedPreference.getID(HomeActivity.this);

        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotification");

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");
        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int num = 0;
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    num++;
                }
                GlobalVars.cartCount = num;

                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                if(tag != null)
                {
                    if(fragment==null){
                        fragment = new AccountFragment();
                        fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
                    }
                }else {
                    if (fragment == null) {
                        fragment = new HomeFragment();
                        fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
                    }
                }
            }
        });


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
                        Fragment fragment4 = new CustomerForumFragment();
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
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        String id = SaveSharedPreference.getID(HomeActivity.this);
        mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");
        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int num = 0;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    num++;
                }
                Log.i("Testing ","here");
                GlobalVars.cartCount = num;
            }
        });
    }
}
