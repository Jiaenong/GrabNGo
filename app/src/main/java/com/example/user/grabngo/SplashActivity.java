package com.example.user.grabngo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.user.grabngo.Admin.ManagerHomeActivity;
import com.example.user.grabngo.Admin.StaffHomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = SaveSharedPreference.getID(SplashActivity.this);
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        if(SaveSharedPreference.getCheckLogin(SplashActivity.this))
        {
            mCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");
            mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int num = 0;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        num++;
                    }
                    SaveSharedPreference.setCartNumber(SplashActivity.this,num);
                    if(SaveSharedPreference.getUserType(SplashActivity.this).equals("customer")) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else if(SaveSharedPreference.getUserType(SplashActivity.this).equals("staff")){
                        Intent intent = new Intent(SplashActivity.this, StaffHomeActivity.class);
                        startActivity(intent);
                    }else if(SaveSharedPreference.getUserType(SplashActivity.this).equals("manager")){
                        Intent intent = new Intent(SplashActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            });
        }else{
            Log.i("Testing ","hello");
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
