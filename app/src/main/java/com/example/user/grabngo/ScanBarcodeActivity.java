package com.example.user.grabngo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.user.grabngo.Class.ProductDetail;
import com.example.user.grabngo.Class.ProductList;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class ScanBarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        scannerView.setAspectTolerance(0.5f);
        setContentView(scannerView);

        if(checkPermission()){
            Toast.makeText(ScanBarcodeActivity.this,"Permission is already granted",Toast.LENGTH_SHORT).show();
        }else{
            requestPermission();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]){
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(ScanBarcodeActivity.this,"Permission granted",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ScanBarcodeActivity.this,"Permission denied",Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to allow access for both permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;

        }}

    @Override
    public void onResume(){
        super.onResume();

        if(checkPermission()){
            if(scannerView==null){
                scannerView = new ZXingScannerView(this);
                scannerView.setAspectTolerance(0.5f);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }else{
            requestPermission();
        }

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        ProductList productList = ProductList.get(ScanBarcodeActivity.this);
        ProductDetail productDetail = productList.getProductBarcode(scanResult);

        if(productDetail!=null){
            Intent intent = new Intent(ScanBarcodeActivity.this,AddToCartActivity.class);
            intent.putExtra("pName",productDetail.getProductName());
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(ScanBarcodeActivity.this,"Invalid Barcode",Toast.LENGTH_SHORT).show();
            scannerView.resumeCameraPreview(ScanBarcodeActivity.this);
        }

    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(ScanBarcodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
