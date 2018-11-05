package com.example.user.grabngo.Admin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.LowStockNotification;
import com.example.user.grabngo.Class.Staff;
import com.example.user.grabngo.LoginActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;
import com.example.user.grabngo.ViewDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class ManagerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(SaveSharedPreference.getCheckAlert(ManagerHomeActivity.this)==false){
            SaveSharedPreference.setCheckAlert(ManagerHomeActivity.this,true);
            scheduleNotification();
        }

        Intent intent = getIntent();
        boolean lowStockAlert = intent.getBooleanExtra("lowStockAlert",false);
        if(lowStockAlert){
            ViewDialog alert = new ViewDialog();
            alert.showDialog(ManagerHomeActivity.this);
        }


        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = new ManagerHomeFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        String id = SaveSharedPreference.getID(ManagerHomeActivity.this);
        FirebaseFirestore.getInstance().collection("Manager").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Staff staff = documentSnapshot.toObject(Staff.class);
                ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
                TextView textView = (TextView)view.findViewById(R.id.textViewName);
                Glide.with(ManagerHomeActivity.this).load(staff.getProfileUrl()).into(imageView);
                textView.setText(staff.getName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = new ManagerHomeFragment();

            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(myFragment instanceof ManagerHomeFragment){
                super.onBackPressed();
            }else{
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        if (id == R.id.nav_home) {

            fragment = new ManagerHomeFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_camera) {

            fragment = new LowStockFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_gallery) {

            fragment = new StockOrderFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_slideshow) {

            fragment = new PromotionFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_share) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManagerHomeActivity.this);
            builder.setTitle("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    AlarmManager alarmMgr = (AlarmManager)ManagerHomeActivity.this.getSystemService(ManagerHomeActivity.this.ALARM_SERVICE);
                    Intent notificationIntent = new Intent(ManagerHomeActivity.this, LowStockNotification.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ManagerHomeActivity.this, 0, notificationIntent, 0);
                    if(alarmMgr!=null){
                        alarmMgr.cancel(pendingIntent);
                        SaveSharedPreference.setCheckAlert(ManagerHomeActivity.this,false);
                    }

                    SaveSharedPreference.clearUser(ManagerHomeActivity.this);
                    Intent intent = new Intent(ManagerHomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else if (id == R.id.nav_manage){

            fragment = new StaffAcountFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_report){

            fragment = new ReportFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    private void scheduleNotification() {

        Intent notificationIntent = new Intent(this, LowStockNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_info_outline_black_24dp);
        return builder.build();
    }

}
