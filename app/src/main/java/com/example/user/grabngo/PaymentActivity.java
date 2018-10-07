package com.example.user.grabngo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;

import org.w3c.dom.Text;

import java.util.List;

import javax.xml.transform.Templates;

public class PaymentActivity extends AppCompatActivity {

    private TextView paymentPrice;
    private ImageButton btnHint;
    private Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final CartList cartList = CartList.get(PaymentActivity.this);
        paymentPrice = (TextView)findViewById(R.id.price_payment);
        paymentPrice.setText("RM " + String.format("%.2f",cartList.totalPrice()));

        final ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.cvv);

        final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setTitle("What is CVV?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.setMessage("CVV is a 3 digits number at the back of your card");
        builder.setView(imageView);
        final AlertDialog alert = builder.create();

        btnHint = (ImageButton)findViewById(R.id.btn_hint);
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alert.show();
            }
        });

        btnPayment = (Button)findViewById(R.id.btn_pay_now);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle("Payment Success");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CartList cartList1 = CartList.get(PaymentActivity.this);
                        List<CartItem> cart = cartList1.getCartItems();
                        cart.removeAll(cart);

                        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                builder.setMessage("Please check your email for the receipt.");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
