package com.example.user.grabngo.Admin;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.Payment;
import com.example.user.grabngo.Class.PaymentDetail;
import com.example.user.grabngo.Class.ProductDetail;
import com.example.user.grabngo.PaymentActivity;
import com.example.user.grabngo.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.grpc.okhttp.internal.framed.Settings;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private Spinner spinnerReportType, spinnerFilter, spinnerMonth, spinnerYear;
    private NavigationView navigationView;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private Date minDate = null, maxDate = null;
    private String month, year;
    private TextView textViewMonth, textViewHide, textViewReportTitle;
    private BarChart barChart;
    private BarChart barChartProduct;
    private ProgressBar progressBar;
    private List<PaymentDetail> paymentList, paymentDetails;
    private List<String> counter;
    private Button btnGenerate;
    private LinearLayout linearLayoutForm, linearLayout3, linearLayout4, graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        setHasOptionsMenu(true);
        getActivity().setTitle("Report");
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Payment");

        setHasOptionsMenu(true);
        spinnerReportType = (Spinner)v.findViewById(R.id.spinner_category);
        spinnerFilter = (Spinner)v.findViewById(R.id.spinner_sortby);
        spinnerMonth = (Spinner)v.findViewById(R.id.spinner_month);
        spinnerYear = (Spinner)v.findViewById(R.id.spinner_year);
        textViewMonth = (TextView)v.findViewById(R.id.textViewMonth);
        textViewHide = (TextView)v.findViewById(R.id.toggle_hide);
        textViewReportTitle = (TextView)v.findViewById(R.id.report_title);
        linearLayoutForm = (LinearLayout)v.findViewById(R.id.linearLayoutForm);
        linearLayout3 = (LinearLayout)v.findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout)v.findViewById(R.id.linearLayout4);
        graph = (LinearLayout)v.findViewById(R.id.graph);
        btnGenerate = (Button)v.findViewById(R.id.btn_generate);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        barChartProduct = (BarChart) v.findViewById(R.id.horizontal_chart);
        barChartProduct.setNoDataTextColor(Color.BLACK);
        barChart = (BarChart)v.findViewById(R.id.chart);
        barChart.setNoDataTextColor(Color.BLACK);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity() , R.array.reportMonth, R.layout.support_simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter1);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int j = thisYear; j >= 2014; j--) {
            years.add(Integer.toString(j));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, years);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter2);
        spinnerYear.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this.getActivity() ,R.array.report, R.layout.support_simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(adapter3);

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i==0){
                    textViewMonth.setVisibility(View.GONE);
                    spinnerFilter.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout4.setVisibility(View.GONE);
                    textViewReportTitle.setText("");
                    barChart.clear();
                    barChartProduct.clear();
                    return;

                }else if(i==1){
                    textViewMonth.setVisibility(View.VISIBLE);
                    spinnerFilter.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout4.setVisibility(View.GONE);

                }else if(i==2){

                    textViewMonth.setVisibility(View.GONE);
                    spinnerFilter.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayout4.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        textViewHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideShow();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int i = spinnerReportType.getSelectedItemPosition();

                if(i==0){
                    return;
                }

                textViewReportTitle.setVisibility(View.VISIBLE);
                hideShow();

                if(i==1){
                    textViewReportTitle.setText(spinnerReportType.getSelectedItem().toString() + " of " + spinnerFilter.getSelectedItem().toString());
                    salesReport();

                }else if(i==2){
                    textViewReportTitle.setText(spinnerReportType.getSelectedItem().toString() + " of " + spinnerMonth.getSelectedItem().toString() + " " + spinnerYear.getSelectedItem().toString());
                    moveToOuterCollection();

                }
            }
        });

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow1 = (android.widget.ListPopupWindow) popup.get(spinnerFilter);
            android.widget.ListPopupWindow popupWindow2 = (android.widget.ListPopupWindow) popup.get(spinnerMonth);
            android.widget.ListPopupWindow popupWindow3 = (android.widget.ListPopupWindow) popup.get(spinnerYear);
            // Set popupWindow height to 500px
            popupWindow1.setHeight(600);
            popupWindow2.setHeight(600);
            popupWindow3.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }


        return v;
    }

    public void moveToOuterCollection(){

        barChartProduct.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        int selectedMonth = spinnerMonth.getSelectedItemPosition()+1;
        year = spinnerYear.getSelectedItem().toString();

        DateFormat df = new SimpleDateFormat("MM yyyy");
        String date = selectedMonth + " " + year;
        int nextMonth = selectedMonth + 1;
        String date2 = nextMonth + " " + year;

        try {
            minDate = df.parse(date);
            maxDate = df.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mCollectionReference.whereGreaterThan("payDate",minDate).whereLessThan("payDate",maxDate).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<String> ref = new ArrayList<>();
                paymentList = new ArrayList<>();
                counter = new ArrayList<>();

                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    ref.add(documentSnapshot.getId().toString());
                }

                if(ref.isEmpty()){
                    barChartProduct.clear();
                    barChartProduct.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                final int size = ref.size();
                for(int i=0; i<ref.size(); i++){
                    mCollectionReference.document(ref.get(i)).collection("PaymentDetail").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            counter.add("plusone");
                            int count=0;
                            for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                PaymentDetail paymentDetail = documentSnapshot.toObject(PaymentDetail.class);
                                paymentList.add(paymentDetail);
                                count++;
                            }


                            if(counter.size()==size){
                                mFirebaseFirestore.collection("ReportProduct").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        WriteBatch batch = mFirebaseFirestore.batch();
                                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                        {
                                            DocumentReference mDocumentReference = documentSnapshot.getReference();
                                            batch.delete(mDocumentReference);
                                        }
                                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                WriteBatch batch1 = mFirebaseFirestore.batch();
                                                for(int i=0; i<paymentList.size(); i++) {
                                                    String id="";
                                                    int num = i+1;
                                                    if(i<9){
                                                        id = "R000"+ num;
                                                    }else{
                                                        id = "R00" + num;
                                                    }

                                                    DocumentReference documentRef = mFirebaseFirestore.collection("ReportProduct").document(id);
                                                    batch1.set(documentRef,paymentList.get(i));
                                                }
                                                batch1.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        bestSellingProduct();
                                                        return;
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });



                            }

                        }
                    });

                }


            }
        });


    }

    public void bestSellingProduct(){

        mFirebaseFirestore.collection("ReportProduct").orderBy("productName").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                paymentDetails = new ArrayList<>();
                List<PaymentDetail> top8Best = new ArrayList<>();
                int i=0;

                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    PaymentDetail paymentDetail = documentSnapshot.toObject(PaymentDetail.class);

                    if(paymentDetails.isEmpty()){
                        paymentDetails.add(paymentDetail);
                    }else {
                        if(paymentDetails.get(i).getProductName().equals(paymentDetail.getProductName())){
                            int quantity = paymentDetails.get(i).getQuantity() + paymentDetail.getQuantity();
                            paymentDetails.get(i).setQuantity(quantity);
                        }else{
                            paymentDetails.add(paymentDetail);
                            i++;
                        }
                    }
                }



                for(int k=0; k<8; k++){
                    PaymentDetail paymentTemp = paymentDetails.get(0);
                    for(int j=1; j<paymentDetails.size(); j++){
                        if(paymentDetails.get(j).getQuantity()>paymentTemp.getQuantity()){
                            paymentTemp = paymentDetails.get(j);
                        }
                    }

                    top8Best.add(paymentTemp);
                    paymentDetails.remove(paymentTemp);
                }

                String[] productName = new String[8];
                List<BarEntry> entries = new ArrayList<>();
                for(int m=7; m>=0; m--){
                    entries.add(new BarEntry(m, top8Best.get(m).getQuantity()));
                    productName[m] = top8Best.get(m).getProductName();
                }

                BarDataSet set = new BarDataSet(entries, "Product Sales");
                int[] color = new int[]{getResources().getColor(R.color.graph_red),
                        getResources().getColor(R.color.graph_orange),
                        getResources().getColor(R.color.graph_yellow),
                        getResources().getColor(R.color.graph_green),
                        getResources().getColor(R.color.graph_blue),
                        getResources().getColor(R.color.graph_purple),
                        getResources().getColor(R.color.graph_indigo),
                        getResources().getColor(R.color.graph_pink)};
                set.setColors(color);

                LegendEntry l1 = new LegendEntry(productName[7],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_red));
                LegendEntry l2 = new LegendEntry(productName[6],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_orange));
                LegendEntry l3 = new LegendEntry(productName[5],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_yellow));
                LegendEntry l4 = new LegendEntry(productName[4],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_green));
                LegendEntry l5 = new LegendEntry(productName[3],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_blue));
                LegendEntry l6 = new LegendEntry(productName[2],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_purple));
                LegendEntry l7 = new LegendEntry(productName[1],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_indigo));
                LegendEntry l8 = new LegendEntry(productName[0],Legend.LegendForm.CIRCLE,10f,2f,null,getResources().getColor(R.color.graph_pink));

                Legend legend = barChartProduct.getLegend();
                legend.setFormSize(5f);
                legend.setWordWrapEnabled(true);
                legend.setCustom(new LegendEntry[]{l1,l2,l3,l4,l5,l6,l7,l8});

                BarData data = new BarData(set);
                data.setValueTextSize(9f);
                data.setBarWidth(0.9f);
                YAxis rightYAxis = barChartProduct.getAxisRight();
                rightYAxis.setDrawLabels(false);
                barChartProduct.getXAxis().setDrawLabels(false);
                barChartProduct.setScaleEnabled(false);
                barChartProduct.setData(data);
                barChartProduct.setDrawGridBackground(true);
                barChartProduct.getDescription().setText("Quantity");
                barChartProduct.getDescription().setPosition(1f,1f);
                barChartProduct.getDescription().setTextSize(6f);
                barChartProduct.setGridBackgroundColor(Color.WHITE);
                barChartProduct.setPinchZoom(true);
                barChartProduct.setFitBars(true); // make the x-axis fit exactly all bars
                barChartProduct.invalidate();
                barChartProduct.setVisibility(View.VISIBLE);

                barChartProduct.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                barChartProduct.saveToGallery("barchart",85);
            }
        });
    }

    public void salesReport(){

        barChartProduct.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        year = spinnerFilter.getSelectedItem().toString();

        DateFormat df = new SimpleDateFormat("MM yyyy");
        String date = "01 " + year;
        int nextYear = Integer.parseInt(year) + 1;
        String date2 = "01 " + nextYear;

        try {
            minDate = df.parse(date);
            maxDate = df.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mCollectionReference.whereGreaterThan("payDate",minDate).whereLessThan("payDate",maxDate).orderBy("payDate", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DateFormat df = new SimpleDateFormat("MM yyyy");
                Float[] monthlySale = new Float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
                String[] monthYear = new String[]{"01 "+year, "02 "+year, "03 "+year, "04 "+year, "05 "+year,
                        "06 "+year, "07 "+year, "08 "+year, "09 "+year, "10 "+year, "11 "+year, "12 "+year,};

                int count=0;
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Payment payment = documentSnapshot.toObject(Payment.class);

                    String payDate = df.format(payment.getPayDate());

                    if(payDate.equals(monthYear[0])){
                        monthlySale[0] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[1])){
                        monthlySale[1] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[2])){
                        monthlySale[2] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[3])){
                        monthlySale[3] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[4])){
                        monthlySale[4] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[5])){
                        monthlySale[5] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[6])){
                        monthlySale[6] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[7])){
                        monthlySale[7] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[8])){
                        monthlySale[8] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[9])){
                        monthlySale[9] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[10])){
                        monthlySale[10] += (float) payment.getTotalPayment();
                    }else if(payDate.equals(monthYear[11])){
                        monthlySale[11] += (float) payment.getTotalPayment();
                    }
                    count++;
                }

                if(count==0){
                    barChart.clear();
                    barChart.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                List<BarEntry> entries = new ArrayList<>();
                for(int i=0; i<12; i++){
                    entries.add(new BarEntry(i, monthlySale[i]));
                }

                barChart.clear();
                String[] month = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

                BarDataSet set = new BarDataSet(entries, "Product Sales");
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                BarData data = new BarData(set);
                data.setValueTextSize(9f);
                data.setBarWidth(0.9f); // set custom bar width
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(month));
                xAxis.setGranularity(1f);
                YAxis yAxis = barChart.getAxisLeft();
                yAxis.setLabelCount(7,false);
                YAxis rightYAxis = barChart.getAxisRight();
                rightYAxis.setDrawLabels(false);
                yAxis.setValueFormatter(new MyYAxisValueFormatter());
                barChart.setData(data);
                barChart.setDrawGridBackground(true);
                barChart.getDescription().setEnabled(false);
                barChart.setGridBackgroundColor(Color.WHITE);
                barChart.setPinchZoom(true);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate();

                barChart.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);


            }
        });
    }

    public void hideShow(){
        if(textViewHide.getText().toString().equals("Hide")){
            linearLayoutForm.setVisibility(View.GONE);
            if(!textViewReportTitle.getText().toString().equals("")){
                textViewReportTitle.setVisibility(View.VISIBLE);
            }
            textViewHide.setText("Show");
            textViewHide.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down_white_24dp, 0, 0, 0);

        }else{
            linearLayoutForm.setVisibility(View.VISIBLE);
            textViewHide.setText("Hide");
            textViewReportTitle.setVisibility(View.GONE);
            textViewHide.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_up_black_24dp, 0, 0, 0);
        }
    }

    public class MyYAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            // format values to 1 decimal digit
            mFormat = new DecimalFormat("###,###,###");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return "RM " + mFormat.format(value);
        }

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pdf:
                int selectedReport = spinnerReportType.getSelectedItemPosition();
                String title="";
                Bitmap bitmap=null;
                if(selectedReport==1 && !barChart.isEmpty()){
                    bitmap = barChart.getChartBitmap();
                    title = spinnerReportType.getSelectedItem().toString() + " of " + spinnerFilter.getSelectedItem().toString();
                }else if(selectedReport==2 && !barChartProduct.isEmpty()){
                    bitmap = barChartProduct.getChartBitmap();
                    title = spinnerReportType.getSelectedItem().toString() + " of " + spinnerMonth.getSelectedItem().toString() + " " + spinnerYear.getSelectedItem().toString();
                }else {
                    Toast.makeText(getActivity(),"No chart data available",Toast.LENGTH_SHORT).show();
                    return true;
                }

                Document doc = new Document();
                String saveLocation = Environment.getExternalStorageDirectory() + "/Chart.pdf";

                try {
                    PdfWriter.getInstance(doc,new FileOutputStream(saveLocation));
                    doc.open();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    image.scaleToFit(500, 700);
                    image.setAbsolutePosition(50, 70);
                    Font f = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
                    Chunk c = new Chunk(title, f);
                    Paragraph p1 = new Paragraph(c);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    doc.add(new Paragraph(p1));
                    doc.add(image);
                    doc.close();

                    Toast.makeText(getActivity(),"PDF is generated",Toast.LENGTH_SHORT).show();
                    File file = new File(saveLocation);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target,"Open file");

                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        Toast.makeText(getActivity(),"There are no PDF viewer installed",Toast.LENGTH_SHORT).show();
                    }

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return true;

            case R.id.action_print:

                String filelocation = Environment.getExternalStorageDirectory() + "/Chart.pdf";
                try
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        PrintManager printManager=(PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
                        PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getActivity(),filelocation );
                        printManager.print("Document", printAdapter,new PrintAttributes.Builder().build());
                    }else{
                        Toast.makeText(getActivity(),"Only android version KitKat and above support printing service",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manager_home,menu);
        return;
    }

}

