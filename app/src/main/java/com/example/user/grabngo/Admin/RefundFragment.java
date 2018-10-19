package com.example.user.grabngo.Admin;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.Class.RefundTemp;
import com.example.user.grabngo.R;
import com.example.user.grabngo.RecyclerTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RefundFragment extends Fragment {

    private NavigationView navigationView;
    private CardView cardViewRefund;
    private List<RefundTemp> refundList;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private RefundAdapter refundAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private Spinner spinnerMonth, spinnerYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_refund, container, false);

        setHasOptionsMenu(true);
        getActivity().setTitle("Refund");

        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycleViewRefund);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        refundList = new ArrayList<>();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Refund");

        spinnerMonth = (Spinner)v.findViewById(R.id.spinner_month);
        spinnerYear = (Spinner)v.findViewById(R.id.spinner_year);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerMonth);
            android.widget.ListPopupWindow popupWindow1 = (android.widget.ListPopupWindow) popup.get(spinnerYear);
            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
            popupWindow1.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity() ,R.array.refundMonth, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        ArrayList<String> years = new ArrayList<String>();
        years.add("------------");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 2014; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, years);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter2);

        spinnerMonth.setSelection(spinnerMonth.getSelectedItemPosition(),false);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerYear.setSelection(spinnerYear.getSelectedItemPosition(),false);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                RefundTemp refund = refundList.get(position);
                Intent intent = new Intent(getContext(),RefundDetailActivity.class);
                intent.putExtra("refundID",refund.getDocumentID());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        refundAdapter = new RefundAdapter(getActivity(),refundList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddRefundActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void show(){
        int selectedMonth = spinnerMonth.getSelectedItemPosition();
        int selectedYear = spinnerYear.getSelectedItemPosition();

        if((selectedMonth == 0 && selectedYear != 0)||(selectedMonth != 0 && selectedYear == 0)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        refundList.clear();

        //DateFormat df = new SimpleDateFormat("MM dd HH:mm:ss yyyy");
        DateFormat df = new SimpleDateFormat("MM yyyy");
        Date date=null, date2=null;

        Query query;
        if(selectedMonth == 0 && selectedYear == 0){
            query = mCollectionReference.orderBy("refundDate", Query.Direction.DESCENDING);
        }else if(selectedMonth == 12){
            String dateMin = selectedMonth + " " + spinnerYear.getSelectedItem().toString();
            try {
                date = df.parse(dateMin);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query = mCollectionReference.whereGreaterThan("refundDate",date).orderBy("refundDate", Query.Direction.DESCENDING);
        }else{
            String dateMin = selectedMonth + " " + spinnerYear.getSelectedItem().toString();
            int selectedMonthPlus = selectedMonth + 1;
            String dateMax = selectedMonthPlus + " " + spinnerYear.getSelectedItem().toString();
            try {
                date = df.parse(dateMin);
                date2 = df.parse(dateMax);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query = mCollectionReference.whereGreaterThan("refundDate",date).whereLessThan("refundDate",date2).orderBy("refundDate", Query.Direction.DESCENDING);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Refund refund = documentSnapshot.toObject(Refund.class);
                    Date date = refund.getRefundDate();
                    DateFormat day = new SimpleDateFormat("dd");
                    DateFormat yearMonth = new SimpleDateFormat("yyyy.MM");
                    DateFormat time = new SimpleDateFormat("hh:mm a");

                    RefundTemp mRefund = new RefundTemp(refund.getProductName(), refund.getCustomerName(), day.format(date),yearMonth.format(date),time.format(date), documentSnapshot.getId());
                    refundList.add(mRefund);
                }
                refundAdapter = new RefundAdapter(getActivity(),refundList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(refundAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        refundList.clear();
        refundAdapter.notifyDataSetChanged();
        show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                Toast.makeText(getActivity(),"Report",Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inventory_menu,menu);
        return;
    }

}
