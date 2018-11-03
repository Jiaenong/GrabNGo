package com.example.user.grabngo.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.LoginActivity;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

class Order {
    private String name, quantity;

    public Order(){

    }

    public Order(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

public class MakeOrderActivity extends AppCompatActivity {

    private LinearLayout linearLayoutOrder, linearLayoutDefaultOrder;
    private CardView cardViewAdd;
    private Spinner spinnerSupplier, spinnerProduct;
    private EditText editTextQuantity, editTextProduct;
    private Button btnOrder;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReferenceProduct, collectionReferenceSupplier;
    private List<Supplier> supplierList;
    private ProgressBar progressBar;
    private ArrayList<String> productList;
    private List<View> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
        MakeOrderActivity.this.setTitle("Make Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayoutOrder = (LinearLayout)findViewById(R.id.linearLayoutOrder);
        linearLayoutDefaultOrder = (LinearLayout)findViewById(R.id.linearLayoutDefaultOrder);
        editTextProduct = (EditText)findViewById(R.id.editTextProduct);
        editTextQuantity = (EditText)findViewById(R.id.editTextQuantity);
        cardViewAdd = (CardView)findViewById(R.id.cardView_add);
        spinnerSupplier = (Spinner)findViewById(R.id.spinner_supplier);
        spinnerProduct = (Spinner)findViewById(R.id.spinner_product);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnOrder = (Button)findViewById(R.id.btn_order);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceProduct = firebaseFirestore.collection("Product");
        collectionReferenceSupplier = firebaseFirestore.collection("Supplier");
        supplierList = new ArrayList<>();
        productList = new ArrayList<String>();
        orderList = new ArrayList<>();

        setSpinnerHeight(spinnerSupplier,800);

        collectionReferenceSupplier.orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> supplierNameList = new ArrayList<String>();

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Supplier supplier = documentSnapshot.toObject(Supplier.class);
                    supplierNameList.add(supplier.getName());
                    Supplier supplierTemp = new Supplier(supplier.getName(), supplier.getEmail(), supplier.getPhone(), supplier.getLocation(),"", documentSnapshot.getId());
                    supplierList.add(supplierTemp);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MakeOrderActivity.this, R.layout.support_simple_spinner_dropdown_item, supplierNameList);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinnerSupplier.setAdapter(adapter);

                spinnerSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        linearLayoutOrder.removeAllViews();
                        orderList.removeAll(orderList);
                        linearLayoutDefaultOrder.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        productList.removeAll(productList);
                        editTextProduct.setText("");
                        editTextQuantity.setText("");

                        collectionReferenceProduct.whereEqualTo("supplierKey",supplierList.get(spinnerSupplier.getSelectedItemPosition()).getDocumentId()).orderBy("productName").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productList.add("Enter Product");
                                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                    productList.add(documentSnapshot.toObject(Product.class).getProductName());
                                }
                                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MakeOrderActivity.this, R.layout.support_simple_spinner_dropdown_item, productList);
                                adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spinnerProduct.setAdapter(adapter1);

                                String productName = getIntent().getStringExtra("productName");

                                if(productName!=null){
                                    firebaseFirestore.collection("Product").whereEqualTo("productName",productName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Product product = queryDocumentSnapshots.getDocuments().get(0).toObject(Product.class);

                                            for(int i =0; i<spinnerSupplier.getAdapter().getCount(); i++){
                                                if(spinnerSupplier.getItemAtPosition(i).equals(product.getProducer())){
                                                    spinnerSupplier.setSelection(i);
                                                }
                                            }

                                            for(int i =0; i<productList.size(); i++){
                                                if(spinnerProduct.getItemAtPosition(i).equals(productName)){
                                                    spinnerProduct.setSelection(i);
                                                    getIntent().removeExtra("productName");
                                                }
                                            }
                                        }
                                    });

                                }

                                linearLayoutDefaultOrder.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerProduct.getSelectedItem().toString().equals("Enter Product")){
                    editTextProduct.setVisibility(View.VISIBLE);
                }else {
                    editTextProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cardViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLayout();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Order> orders = new ArrayList<>();
                String content = "";

                if(validation()){
                    createPDF();
                     Order order = new Order();

                    content = "Hi, we are from Econsave and would like to make an order with your company. \n\n Below are products we would like to order: \n\n";
                    if(spinnerProduct.getSelectedItemPosition()==0){
                        order.setName(editTextProduct.getText().toString());
                        content += "1. " + editTextProduct.getText().toString() + "\t";
                    }else {
                        order.setName(spinnerProduct.getSelectedItem().toString());
                        content += "1. " + spinnerProduct.getSelectedItem().toString() + "\t";
                    }
                    content += " - " + editTextQuantity.getText().toString() + " units\n";
                    order.setQuantity(editTextQuantity.getText().toString());
                    orders.add(order);

                    for(int i=0; i<orderList.size(); i++){
                        View viewTemp = orderList.get(i);

                        Spinner spinnerProductTemp = (Spinner) viewTemp.findViewById(R.id.spinner_product);
                        EditText editTextQtyTemp = (EditText)viewTemp.findViewById(R.id.editTextQuantity);
                        EditText editTextProductTemp = (EditText)viewTemp.findViewById(R.id.editTextProduct);

                        int num = i+2;
                        if(spinnerProductTemp.getSelectedItemPosition()==0){
                            order.setName(editTextProductTemp.getText().toString());
                            content += num + ". " + editTextProductTemp.getText().toString() + "\t";
                        }else {
                            order.setName(spinnerProductTemp.getSelectedItem().toString());
                            content += num + ". " + spinnerProductTemp.getSelectedItem().toString() + "\t";
                        }
                        content += " - " + editTextQtyTemp.getText().toString() + " units\n";
                        order.setQuantity(editTextQtyTemp.getText().toString());
                        orders.add(order);

                    }

                    String email = supplierList.get(spinnerSupplier.getSelectedItemPosition()).getEmail();
                    String completePath = Environment.getExternalStorageDirectory() + "/Order.pdf";

                    File file = new File(completePath);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("*/*");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Econsave: Purchase Order");
                    emailIntent.putExtra(Intent.EXTRA_TEXT   , content);

                    try {
                        startActivityForResult(createEmailOnlyChooserIntent(emailIntent, "Send via email"),2);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MakeOrderActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    public void createPDF(){
        String saveLocation = Environment.getExternalStorageDirectory() + "/Order.pdf";

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(saveLocation));
            doc.setPageSize(PageSize.A4);
            Drawable d = getResources ().getDrawable (R.drawable.econsavelogo);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();
            img = Image.getInstance(byteArray);
            img.scaleToFit(450, 80);
            float x = (PageSize.A4.getWidth() - img.getScaledWidth()) / 2;
            img.setAbsolutePosition(x, PageSize.A4.getHeight()-120);
            doc.open();
            doc.add(img);
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("\n\n\nPurchase Order\n", f);
            Paragraph p1 = new Paragraph(c);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(new Paragraph(p1));

            PdfPTable table = new PdfPTable(2);
            table.setTotalWidth(600f);
            String s = "\nEconsave\nG01, 67,\nJalan Taman Ibu Kota,\nTaman Danau Kota,\n53300 Kuala Lumpur\n\nPhone number: (123)456-8172\nFax Number: (123)901-9281\n\n";
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            String s2 = "\nIssued by: Lee Jia Jun\n\nDate issued: "+df.format(date);

            Font tableFont = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.NORMAL, BaseColor.BLACK);
            //Cell One(Econsave info)
            PdfPCell cell = new PdfPCell(new Phrase(s,tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);

            //Cell Two(Issue By)
            PdfPCell cell2 = new PdfPCell(new Phrase(s2,tableFont));
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setBorder(Rectangle.NO_BORDER);

            //Add to PDF
            table.addCell(cell);
            table.addCell(cell2);
            doc.add(table);

            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.BOLD, BaseColor.BLACK);
            //Order Detail

            PdfPTable orderTable = new PdfPTable(new float[]{1f,5f,2f});
            orderTable.setTotalWidth(600f);
            PdfPCell headerCell = new PdfPCell(new Phrase(" "));
            headerCell.setBackgroundColor(new BaseColor(220,220,220));

            PdfPCell headerCell2 = new PdfPCell(new Phrase("Product Description",headerFont));
            headerCell2.setBackgroundColor(new BaseColor(220,220,220));
            headerCell2.setVerticalAlignment(Element.ALIGN_CENTER);
            headerCell2.setPaddingBottom(5f);
            headerCell2.setPaddingLeft(10f);

            PdfPCell headerCell3 = new PdfPCell(new Phrase("Quantity",headerFont));
            headerCell3.setBackgroundColor(new BaseColor(220,220,220));
            headerCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell3.setVerticalAlignment(Element.ALIGN_CENTER);
            headerCell3.setPaddingBottom(5f);
            headerCell3.setPaddingRight(10f);

            orderTable.setHeaderRows(1);
            orderTable.addCell(headerCell);
            orderTable.addCell(headerCell2);
            orderTable.addCell(headerCell3);

            PdfPCell orderCell = new PdfPCell(new Phrase("1."));
            orderCell.setPaddingLeft(10f);

            PdfPCell orderCell2;
            if(spinnerProduct.getSelectedItemPosition()==0){
                orderCell2 = new PdfPCell(new Phrase(editTextProduct.getText().toString(),tableFont));
                orderCell2.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell2.setPaddingBottom(5f);
                orderCell2.setPaddingLeft(10f);

            }else {
                orderCell2 = new PdfPCell(new Phrase(spinnerProduct.getSelectedItem().toString(),tableFont));
                orderCell2.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell2.setPaddingBottom(5f);
                orderCell2.setPaddingLeft(10f);
            }

            PdfPCell orderCell3 = new PdfPCell(new Phrase(editTextQuantity.getText().toString(),tableFont));
            orderCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            orderCell3.setVerticalAlignment(Element.ALIGN_CENTER);
            orderCell3.setPaddingBottom(5f);
            orderCell3.setPaddingRight(10f);

            orderTable.addCell(orderCell);
            orderTable.addCell(orderCell2);
            orderTable.addCell(orderCell3);

            for(int i=0; i<orderList.size();i++){
                View viewTemp = orderList.get(i);

                Spinner spinnerProductTemp = (Spinner) viewTemp.findViewById(R.id.spinner_product);
                EditText editTextQtyTemp = (EditText)viewTemp.findViewById(R.id.editTextQuantity);
                EditText editTextProductTemp = (EditText)viewTemp.findViewById(R.id.editTextProduct);

                int num = i+2;
                PdfPCell orderCell4 = new PdfPCell(new Phrase(num+"."));
                orderCell4.setPaddingLeft(10f);

                PdfPCell orderCell5;
                if(spinnerProductTemp.getSelectedItemPosition()==0){
                    orderCell5 = new PdfPCell(new Phrase(editTextProductTemp.getText().toString(),tableFont));
                    orderCell5.setVerticalAlignment(Element.ALIGN_CENTER);
                    orderCell5.setPaddingBottom(5f);
                    orderCell5.setPaddingLeft(10f);

                }else {
                    orderCell5 = new PdfPCell(new Phrase(spinnerProductTemp.getSelectedItem().toString(),tableFont));
                    orderCell5.setVerticalAlignment(Element.ALIGN_CENTER);
                    orderCell5.setPaddingBottom(5f);
                    orderCell5.setPaddingLeft(10f);
                }

                PdfPCell orderCell6 = new PdfPCell(new Phrase(editTextQtyTemp.getText().toString(),tableFont));
                orderCell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                orderCell6.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell6.setPaddingBottom(5f);
                orderCell6.setPaddingRight(10f);

                orderTable.addCell(orderCell4);
                orderTable.addCell(orderCell5);
                orderTable.addCell(orderCell6);
            }

            doc.add(orderTable);
            doc.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Intent createEmailOnlyChooserIntent(Intent source,
                                               CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "info@domain.com", null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));

            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    private void setSpinnerHeight(Spinner spinner, int height){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
            // Set popupWindow height to 500px
            popupWindow.setHeight(height);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }

    private void addLayout() {
        final View layout = LayoutInflater.from(MakeOrderActivity.this).inflate(R.layout.cardview_order, linearLayoutOrder, false);

        final Spinner spinnerProductExtra = (Spinner) layout.findViewById(R.id.spinner_product);
        EditText editTextQtyExtra = (EditText)layout.findViewById(R.id.editTextQuantity);
        final EditText editTextProductExtra = (EditText)layout.findViewById(R.id.editTextProduct);
        ImageButton imageButtonClose = (ImageButton)layout.findViewById(R.id.button_close);
        TextView textViewOrder = (TextView)layout.findViewById(R.id.textViewOrder);

        int number = orderList.size()+2;
        textViewOrder.setText("Order "+number);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MakeOrderActivity.this, R.layout.support_simple_spinner_dropdown_item, productList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerProductExtra.setAdapter(adapter);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutOrder.removeView(layout);
                orderList.remove(layout);
                for(int i=0; i<orderList.size(); i++){
                    TextView mTextViewOrder = orderList.get(i).findViewById(R.id.textViewOrder);

                    int number = i+2;
                    mTextViewOrder.setText("Order "+number);
                }
            }
        });

        spinnerProductExtra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerProductExtra.getSelectedItem().toString().equals("Enter Product")){
                    editTextProductExtra.setVisibility(View.VISIBLE);
                }else {
                    editTextProductExtra.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        orderList.add(layout);
        linearLayoutOrder.addView(layout);
    }

    public boolean validation(){

        String errMsg = "";
        boolean noError = true;

        if(spinnerProduct.getSelectedItemPosition()==0 && editTextProduct.getText().toString().equals("")){
            errMsg = "Order 1: Please enter product";
        }else if(editTextQuantity.getText().toString().equals("")){
            errMsg = "Order 1: Order quantity is empty. Please fill in to proceed";
        }else{
            for(int i=0; i<orderList.size(); i++){
                View viewTemp = orderList.get(i);

                Spinner spinnerProductTemp = (Spinner) viewTemp.findViewById(R.id.spinner_product);
                EditText editTextQtyTemp = (EditText)viewTemp.findViewById(R.id.editTextQuantity);
                EditText editTextProductTemp = (EditText)viewTemp.findViewById(R.id.editTextProduct);

                int number = i+2;
                if(spinnerProductTemp.getSelectedItemPosition()==0 && editTextProductTemp.getText().toString().equals("")){
                    errMsg = "Order " + number + ": Please enter product";
                    break;
                }else if(editTextQtyTemp.getText().toString().equals("")){
                    errMsg = "Order " + number + ": Order quantity is empty. Please fill in to proceed";
                    break;
                }
            }
        }

        if(!errMsg.equals("")){
            noError = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MakeOrderActivity.this, R.style.AlertDialogCustom));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setTitle("Error");
            builder.setMessage(errMsg);
            AlertDialog alert = builder.create();
            alert.show();
        }

        return noError;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            }else{
                finish();
            }
        }
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
