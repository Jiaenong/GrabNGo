package com.example.user.grabngo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.grabngo.Class.Payment;
import com.example.user.grabngo.Class.PaymentDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item1Activity extends AppCompatActivity {

    private TextView textViewTotalPayment;
    private RecyclerView recyclerViewPaymentDetail;
    private ProgressBar progressBarDetail;
    private LinearLayout layout1 , layout2, layout3;

    private List<PaymentDetail> pdList;
    private DetailAdapter adapter;
    private String id;
    private double totalPayment = 0;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, nCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item1);

        setTitle("Purchase History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String date = intent.getStringExtra("payDate");

        textViewTotalPayment = (TextView)findViewById(R.id.textViewTotalPayment);
        recyclerViewPaymentDetail = (RecyclerView)findViewById(R.id.recycleViewPaymentDetail);
        progressBarDetail = (ProgressBar)findViewById(R.id.progressBarDetail);
        layout1 = (LinearLayout)findViewById(R.id.layout1);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        layout3 = (LinearLayout)findViewById(R.id.layout3);
        pdList = new ArrayList<>();

        progressBarDetail.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);

        Log.i("Testing ",date);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        nCollectionReference = mFirebaseFirestore.collection("Payment");
        nCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Payment payment = documentSnapshot.toObject(Payment.class);
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String daTe = formatDate.format(payment.getPayDate());
                    if(daTe.equals(date))
                    {
                        Log.i("testing ", daTe);
                        id = documentSnapshot.getId();
                        totalPayment = payment.getTotalPayment();
                    }
                }
                mCollectionReference = mFirebaseFirestore.collection("Payment").document(id).collection("PaymentDetail");
                mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            PaymentDetail paymentDetail = documentSnapshot.toObject(PaymentDetail.class);
                            String itemName = paymentDetail.getProductName();
                            int quantity = paymentDetail.getQuantity();
                            double subTotal = paymentDetail.getItemPrice();
                            PaymentDetail pDetail = new PaymentDetail(itemName, quantity, subTotal);
                            pdList.add(pDetail);
                        }
                        adapter = new DetailAdapter(pdList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerViewPaymentDetail.setLayoutManager(mLayoutManager);
                        recyclerViewPaymentDetail.setItemAnimator(new DefaultItemAnimator());
                        recyclerViewPaymentDetail.setAdapter(adapter);
                        textViewTotalPayment.setText("RM "+totalPayment);
                        progressBarDetail.setVisibility(View.GONE);
                        layout1.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.VISIBLE);
                        layout3.setVisibility(View.VISIBLE);
                    }
                });
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

    public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.MyViewHolder>{
        private List<PaymentDetail> detailList;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View detailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item1_view,parent,false);
            return new MyViewHolder(detailView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PaymentDetail paymentDetail = detailList.get(position);
            double subTotal = 0;
            holder.textViewItemName.setText(paymentDetail.getProductName());
            holder.textViewItemQuantity.setText(paymentDetail.getQuantity()+"");
            subTotal += paymentDetail.getItemPrice()*paymentDetail.getQuantity();
            holder.textViewItemPrice.setText(String.format("%.2f",subTotal));
        }

        @Override
        public int getItemCount() {
            return detailList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewItemName, textViewItemQuantity, textViewItemPrice;

            public MyViewHolder(View view)
            {
                super(view);
                textViewItemName = (TextView)view.findViewById(R.id.textViewItemName);
                textViewItemQuantity = (TextView)view.findViewById(R.id.textViewItemQuantity);
                textViewItemPrice = (TextView)view.findViewById(R.id.textViewItemPrice);
            }
        }

        public DetailAdapter(List<PaymentDetail> dList)
        {
            detailList = dList;
        }
    }
}
