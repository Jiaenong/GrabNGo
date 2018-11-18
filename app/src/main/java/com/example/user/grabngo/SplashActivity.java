package com.example.user.grabngo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob();
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }
}
