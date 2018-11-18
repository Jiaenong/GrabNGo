package com.example.user.grabngo;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
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
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public String tag;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private final int MY_PERMISSIONS_REQUEST = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent2 = getIntent();
        tag = intent2.getStringExtra("tag");

        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotification");

        mFirebaseFirestore = FirebaseFirestore.getInstance();

        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.CAMERA);
        }
        if(!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
        }

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
                SaveSharedPreference.setCartNumber(HomeActivity.this,num);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[i];
                        if(Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                        if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }

                        if(Manifest.permission.CAMERA   .equals(permission)) {
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }
}
