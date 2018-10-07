package com.example.user.grabngo.Admin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.grabngo.R;

public class ManagerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

}