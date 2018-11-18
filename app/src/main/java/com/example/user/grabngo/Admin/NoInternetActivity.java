package com.example.user.grabngo.Admin;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.grabngo.R;

public class NoInternetActivity extends AppCompatActivity {

    private Button btnRetry;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        btnRetry = findViewById(R.id.button_retry);
        progressBar = findViewById(R.id.progressBar);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                        //For 3G check
                        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                                .isConnectedOrConnecting();
                        //For WiFi Check
                        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                                .isConnectedOrConnecting();

                        if(is3g || isWifi){
                            finish();
                        }else{
                            Toast.makeText(NoInternetActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    }
                }, 3000);   //5 seconds

            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(NoInternetActivity.this);
        builder.setTitle("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NoInternetActivity.this.finishAffinity();
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
}
