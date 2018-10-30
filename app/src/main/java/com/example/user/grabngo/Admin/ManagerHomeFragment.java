package com.example.user.grabngo.Admin;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.grabngo.Class.Payment;
import com.example.user.grabngo.R;
import com.example.user.grabngo.ViewDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerHomeFragment extends Fragment {

    private NavigationView navigationView;
    private CardView cardViewLowStockAlert, cardViewLowStock, cardViewStockOrdering, cardViewPromotion, cardViewReport;
    private TextView todaySales, yesterdaySales;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manager_home, container, false);

        getActivity().setTitle("Home");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        todaySales = (TextView)v.findViewById(R.id.textViewTodaySales);
        yesterdaySales = (TextView)v.findViewById(R.id.textViewYesterSales);
        cardViewLowStock = (CardView)v.findViewById(R.id.cardview_low_stock);
        cardViewStockOrdering = (CardView)v.findViewById(R.id.cardview_stock_order);
        cardViewPromotion = (CardView)v.findViewById(R.id.cardview_promotion);
        cardViewReport = (CardView)v.findViewById(R.id.cardview_report);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Payment");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        Date today = cal.getTime();

        cal.add(Calendar.DATE,-1);
        Date yesterday = cal.getTime();

        cal.add(Calendar.DATE,2);
        Date tomorrow = cal.getTime();

        mCollectionReference.whereGreaterThan("payDate",today).whereLessThan("payDate",tomorrow).orderBy("payDate", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                double sales = 0;
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Payment payment = documentSnapshot.toObject(Payment.class);
                    sales += payment.getTotalPayment();
                }

                if(sales==0){
                    todaySales.setText("RM 0.00");
                }else {
                    todaySales.setText("RM " + String.format("%.2f",sales));
                }
            }
        });

        mCollectionReference.whereGreaterThan("payDate",yesterday).whereLessThan("payDate",today).orderBy("payDate", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                double sales = 0;
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Payment payment = documentSnapshot.toObject(Payment.class);
                    sales += payment.getTotalPayment();
                }

                if(sales==0){
                    yesterdaySales.setText("RM 0.00");
                }else {
                    yesterdaySales.setText("RM " + String.format("%.2f",sales));
                }

            }
        });


        final FragmentManager fm = getFragmentManager();

        cardViewLowStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LowStockFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewStockOrdering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new StockOrderFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ReportFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        cardViewPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PromotionFragment();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        return v;
    }

}
