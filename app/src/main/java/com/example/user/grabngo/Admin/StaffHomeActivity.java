package com.example.user.grabngo.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.grabngo.HomeFragment;
import com.example.user.grabngo.LoginActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.SaveSharedPreference;

public class StaffHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = new StaffHomeFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = new StaffHomeFragment();

            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(myFragment instanceof StaffHomeFragment){
                super.onBackPressed();
            }else{
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        if(id == R.id.nav_home) {
            fragment = new StaffHomeFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_inventory) {
            fragment = new InventoryFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_refund) {
            fragment = new RefundFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_forum) {
            fragment = new ForumFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_account) {
            fragment = new StaffAcountFragment();
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
            builder.setTitle("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SaveSharedPreference.clearUser(StaffHomeActivity.this);
                    Intent intent = new Intent(StaffHomeActivity.this,LoginActivity.class);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
