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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.user.grabngo.Class.Cart;
import com.example.user.grabngo.Class.CartItem;
import com.example.user.grabngo.Class.CartList;
import com.example.user.grabngo.Class.Customer;
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
import com.google.firebase.firestore.WriteBatch;

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
    private ProgressBar progressBar;
    private ScrollView scrollViewPayment;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, nCollectionReference, pCollectionReference;
    private DocumentReference mDocumentReference, nDocumentReference, pDocumentReference;

    private List<String> items, itemsprice;
    private List<Integer> amounts;
    private String paymentID;
    private int qty;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressDialog = new ProgressDialog(this);
        editTextCardNumber = (EditText)findViewById(R.id.editTextCardNumber);
        editTextCardName = (EditText)findViewById(R.id.editTextCardName);
        editTextExpDate = (EditText)findViewById(R.id.editTextExpDate);
        editTextCVV = (EditText)findViewById(R.id.editTextCVV);
        switchSave = (Switch)findViewById(R.id.switchSave);
        progressBar = (ProgressBar)findViewById(R.id.progressBarPayment);
        scrollViewPayment = (ScrollView)findViewById(R.id.scrollViewPayment);

        progressBar.setVisibility(View.VISIBLE);
        scrollViewPayment.setVisibility(View.GONE);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        nCollectionReference = mFirebaseFirestore.collection("Payment");
        nCollectionReference.orderBy("payDate", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                {
                    paymentID = "PA0001";
                    progressBar.setVisibility(View.GONE);
                    scrollViewPayment.setVisibility(View.VISIBLE);
              }else{
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        String id = documentSnapshot.getId();
                        int num = Integer.parseInt(id.substring(4));
                        num++;
                        if(num > 9)
                        {
                            int numss = Integer.parseInt(id.substring(3));
                            numss++;
                            paymentID = "PA00"+numss;
                            progressBar.setVisibility(View.GONE);
                            scrollViewPayment.setVisibility(View.VISIBLE);
                        }else {
                            paymentID = "PA000" + num;
                            progressBar.setVisibility(View.GONE);
                            scrollViewPayment.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }
        });

        Intent intent = getIntent();
        String text = intent.getStringExtra("totalPrice");

        items = new ArrayList<>();
        itemsprice = new ArrayList<>();
        amounts = new ArrayList<>();

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

                progressDialog.setMessage("Processing...");
                progressDialog.show();
                items.clear();
                amounts.clear();
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

                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                {
                                    Cart cart = documentSnapshot.toObject(Cart.class);
                                    String key = cart.getProductRef();
                                    int quantity = cart.getQuantity();
                                    amounts.add(quantity);
                                    mFirebaseFirestore.collection("Product").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Product product = documentSnapshot.toObject(Product.class);

                                            double price = Double.parseDouble(product.getPrice());
                                            if(product.getDiscount()!=0){
                                                double discountPercent = (100 - product.getDiscount())*0.01;
                                                price = price * discountPercent;
                                            }
                                            items.add(product.getProductName());
                                            itemsprice.add(product.getPrice());
                                            PaymentDetail paymentDetail = new PaymentDetail(product.getProductName(), quantity, price);
                                            pCollectionReference.add(paymentDetail).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    return;
                                                }
                                            });

                                            nDocumentReference = mFirebaseFirestore.collection("Product").document(key);
                                            int quantityStock = product.getStockAmount();
                                            quantityStock -= quantity;
                                            nDocumentReference.update("stockAmount",quantityStock).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    return;
                                                }
                                            });

                                        }
                                    });


                                }

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
                                //sendMail();
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
                WriteBatch batch = mFirebaseFirestore.batch();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    DocumentReference mDocumentReference = documentSnapshot.getReference();
                    batch.delete(mDocumentReference);
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

    }

    private void sendMail()
    {
        String id = SaveSharedPreference.getID(PaymentActivity.this);
        pDocumentReference = mFirebaseFirestore.document("Customer/"+id);
        pDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i("Testing ", "hello");
                Customer customer = documentSnapshot.toObject(Customer.class);
                String email = customer.getEmail();
                String subject = "Payment successfull ! This is your receipt";
                String message = "";
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i<items.size(); i++)
                {
                    Double amount = Double.parseDouble(itemsprice.get(i)) * amounts.get(i);
                    sb.append(items.get(i)+": "+amounts.get(i)+"  "+"RM"+amount+"\n");
                    message = sb.toString()+"\n"+"Total Payment: "+paymentPrice.getText().toString();
                }
                SendeMail sm = new SendeMail(PaymentActivity.this, email, subject, message);
                sm.execute();
            }
        });
        return;
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
