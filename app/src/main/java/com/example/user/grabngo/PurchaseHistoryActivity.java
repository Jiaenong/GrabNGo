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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private List<Payment> paymentList;
    private HistoryAdapter adapter;
    private ProgressBar progressBarHistory;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        setTitle("Purchase History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Payment");

        paymentList = new ArrayList<>();
        recyclerViewHistory = (RecyclerView)findViewById(R.id.recycleViewHistory);
        progressBarHistory = (ProgressBar)findViewById(R.id.progressBarHistory);

        recyclerViewHistory.setVisibility(View.GONE);
        progressBarHistory.setVisibility(View.VISIBLE);

        recyclerViewHistory.addOnItemTouchListener(new RecyclerTouchListener(PurchaseHistoryActivity.this, recyclerViewHistory, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Payment payment = paymentList.get(position);
                Intent intent = new Intent(PurchaseHistoryActivity.this, Item1Activity.class);
                SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String date = formatDate.format(payment.getPayDate());
                intent.putExtra("payDate",date);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        String id = SaveSharedPreference.getID(PurchaseHistoryActivity.this);
        mCollectionReference.whereEqualTo("customerKey",id).orderBy("payDate", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Payment payment = documentSnapshot.toObject(Payment.class);
                    Date date = payment.getPayDate();
                    String customerKey = payment.getCustomerKey();
                    double price = payment.getTotalPayment();
                    Payment payment1 = new Payment(date, price, customerKey);
                    paymentList.add(payment1);
                }
                adapter = new HistoryAdapter(paymentList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewHistory.setLayoutManager(mLayoutManager);
                recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
                recyclerViewHistory.setAdapter(adapter);
                recyclerViewHistory.setVisibility(View.VISIBLE);
                progressBarHistory.setVisibility(View.GONE);
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

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
        private List<Payment> paymentList;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View historyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_history, parent, false);
            return new MyViewHolder(historyView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Payment payment = paymentList.get(position);
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat nameDay = new SimpleDateFormat("EEEE");
            String date = formatDate.format(payment.getPayDate());
            String realNameDay = nameDay.format(payment.getPayDate());
            String day = date.substring(0,2);
            String realDate = date.substring(3);
            holder.textViewDay.setText(day);
            holder.textViewDate.setText(realDate);
            holder.textViewWeek.setText(realNameDay);
            holder.textViewPrice.setText("RM " + String.format("%.2f",payment.getTotalPayment()));
        }

        @Override
        public int getItemCount() {
            return paymentList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewDay, textViewDate, textViewWeek, textViewPrice;

            public MyViewHolder(View view)
            {
                super(view);
                textViewDay = (TextView)view.findViewById(R.id.textViewDay);
                textViewDate = (TextView)view.findViewById(R.id.textViewDate);
                textViewWeek = (TextView)view.findViewById(R.id.textViewWeek);
                textViewPrice = (TextView)view.findViewById(R.id.textViewPrice);
            }
        }

        public HistoryAdapter(List<Payment> payList)
        {
            paymentList = payList;
        }
    }
}
