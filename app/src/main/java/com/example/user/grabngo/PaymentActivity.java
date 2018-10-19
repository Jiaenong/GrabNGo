package com.example.user.grabngo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;
import com.example.user.grabngo.Class.Payment;
import com.example.user.grabngo.Class.PaymentDetail;
import com.example.user.grabngo.Class.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.Templates;

public class PaymentActivity extends AppCompatActivity {

    private TextView paymentPrice;
    private ImageButton btnHint;
    private Button btnPayment;
    private EditText editTextCardNumber, editTextCardName, editTextExpDate, editTextCVV;
    private Switch switchSave;
    private ProgressDialog progressDialog;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, nCollectionReference, pCollectionReference;
    private DocumentReference mDocumentReference, nDocumentReference;

    private List<String> items;
    private List<Integer> amounts;
    private String paymentID;
    private int qty;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        nCollectionReference = mFirebaseFirestore.collection("Payment");
        nCollectionReference.orderBy("payDate", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                {
                    paymentID = "PA0001";
                }else{
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        String id = documentSnapshot.getId();
                        int num = Integer.parseInt(id.substring(4));
                        num++;
                        paymentID = "PA000"+num;
                        break;
                    }
                }
            }
        });

        Intent intent = getIntent();
        String text = intent.getStringExtra("totalPrice");

        items = new ArrayList<>();
        amounts = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        editTextCardNumber = (EditText)findViewById(R.id.editTextCardNumber);
        editTextCardName = (EditText)findViewById(R.id.editTextCardName);
        editTextExpDate = (EditText)findViewById(R.id.editTextExpDate);
        editTextCVV = (EditText)findViewById(R.id.editTextCVV);
        switchSave = (Switch)findViewById(R.id.switchSave);

        if(SaveSharedPreference.getCheckSave(PaymentActivity.this))
        {
            editTextCardNumber.setText(SaveSharedPreference.getCardNumber(PaymentActivity.this));
            editTextCardName.setText(SaveSharedPreference.getCardName(PaymentActivity.this));
            editTextExpDate.setText(SaveSharedPreference.getExpDate(PaymentActivity.this));
            editTextCVV.setText(SaveSharedPreference.getCVV(PaymentActivity.this));
            switchSave.setChecked(true);
        }

        paymentPrice = (TextView)findViewById(R.id.price_payment);
        paymentPrice.setText(text);

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
                if(progressDialog.isShowing())
                    progressDialog.setMessage("Processing...");
                progressDialog.show();
                items.clear();
                if(switchSave.isChecked())
                {
                    Boolean save = true;
                    String cardNumber = editTextCardNumber.getText().toString();
                    String cardName = editTextCardName.getText().toString();
                    String expDate = editTextExpDate.getText().toString();
                    String cvv = editTextCVV.getText().toString();

                    SaveSharedPreference.setCardNumber(PaymentActivity.this,cardNumber);
                    SaveSharedPreference.setCardName(PaymentActivity.this,cardName);
                    SaveSharedPreference.setExpDate(PaymentActivity.this, expDate);
                    SaveSharedPreference.setCVV(PaymentActivity.this, cvv);
                    SaveSharedPreference.setCheckSave(PaymentActivity.this, save);
                }else{
                    SaveSharedPreference.clearData(PaymentActivity.this);
                }
                final String id = SaveSharedPreference.getID(PaymentActivity.this);
                Calendar calendar = Calendar.getInstance();
                final java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

                String text = paymentPrice.getText().toString();
                double price = Double.parseDouble(text.substring(2));
                Payment payment = new Payment(currentTimestamp, price, id);
                mDocumentReference = mFirebaseFirestore.document("Payment/"+paymentID);
                mDocumentReference.set(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pCollectionReference = mFirebaseFirestore.collection("Payment").document(paymentID).collection("PaymentDetail");
                        mCollectionReference = mFirebaseFirestore.collection("Customer/"+id+"/Cart");
                        mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String key = "";
                                int quantity = 0;
                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                {
                                    CartItem cartItem = documentSnapshot.toObject(CartItem.class);
                                    String item = cartItem.getProductname();
                                    key = cartItem.getProductKey();
                                    qty = cartItem.getQuantity();
                                    quantity = cartItem.getQuantity();
                                    double price = cartItem.getPrice();
                                    PaymentDetail paymentDetail = new PaymentDetail(item, quantity, price);
                                    pCollectionReference.add(paymentDetail).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            return;
                                        }
                                    });
                                }
                                nDocumentReference = mFirebaseFirestore.document("Product/"+key);
                                nDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Product product = documentSnapshot.toObject(Product.class);
                                        int quantity = product.getStockAmount();
                                        quantity -= qty;
                                        nDocumentReference.update("stockAmount",quantity);
                                    }
                                });
                            }
                        });
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                        builder.setTitle("Payment Success");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int batchSize = 3;
                                deleteCollection(batchSize);

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

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void deleteCollection(final int batchSize) {
        String id = SaveSharedPreference.getID(PaymentActivity.this);
        nCollectionReference = mFirebaseFirestore.collection("Customer").document(id).collection("Cart");
        nCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int deleted = 0;
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    documentSnapshot.getReference().delete();
                    ++deleted;
                }
                if(deleted >= batchSize)
                {
                    deleteCollection(batchSize);
                }
            }
        });

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
