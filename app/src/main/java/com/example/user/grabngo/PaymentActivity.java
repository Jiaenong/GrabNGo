package com.example.user.grabngo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Spinner spinnerCardType;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, nCollectionReference, pCollectionReference;
    private DocumentReference mDocumentReference, nDocumentReference, pDocumentReference;

    private List<String> items, itemsprice;
    private List<Integer> amounts;
    private String paymentID;
    private int qty;
    private String expDates;

    private DatePickerDialog datePickerDialog;

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
        spinnerCardType = (Spinner)findViewById(R.id.spinnerCardType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PaymentActivity.this ,R.array.cardss, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCardType.setAdapter(adapter);

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
            String number = SaveSharedPreference.getCardNumber(PaymentActivity.this);
            editTextCardNumber.setText("XXXXXXXXXXXX"+number.substring(12));
            editTextCardName.setText(SaveSharedPreference.getCardName(PaymentActivity.this));
            editTextExpDate.setText(SaveSharedPreference.getExpDate(PaymentActivity.this));
            expDates = SaveSharedPreference.getSaveDate(PaymentActivity.this);
            spinnerCardType.setSelection(SaveSharedPreference.getCardType(PaymentActivity.this));
            switchSave.setChecked(true);
        }

        editTextExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(PaymentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        expDates = dayOfMonth+"-"+month+"-"+year;
                        editTextExpDate.setText((month+1)+"/"+year);
                    }
                },year,month,day);
                ((ViewGroup)datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day","id","android")).setVisibility(View.GONE);
                datePickerDialog.show();
            }
        });

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
                String cardNumber = editTextCardNumber.getText().toString();
                String cardName = editTextCardName.getText().toString();
                String expDate = editTextExpDate.getText().toString();
                String cvv = editTextCVV.getText().toString();
                int cardType = spinnerCardType.getSelectedItemPosition();

                if(cardName.equals("")||cardNumber.equals("")||expDate.equals("")||cvv.equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                    builder.setTitle("Payment Fail");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editTextCardName.setText("");
                            editTextCardNumber.setText("");
                            editTextExpDate.setText("");
                            editTextCVV.setText("");
                            alert.cancel();
                        }
                    });
                    builder.setMessage("All field are required to enter !");
                    AlertDialog alert = builder.create();
                    alert.show();
                }else if(cardNumber.length() != 16)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                    builder.setTitle("Payment Fail");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editTextCardName.setFocusable(true);
                            editTextCVV.setText("");
                            alert.cancel();
                        }
                    });
                    builder.setMessage("The Card Number should be 16 digit !");
                    AlertDialog alert = builder.create();
                    alert.show();
                }else if(checkDate() == false)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                    builder.setTitle("Payment Fail");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editTextExpDate.setFocusable(true);
                            editTextCVV.setText("");
                            alert.cancel();
                        }
                    });
                    builder.setMessage("The card has already expired !");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    if (switchSave.isChecked()) {
                        Boolean save = true;
                        SaveSharedPreference.setCardNumber(PaymentActivity.this, cardNumber);
                        SaveSharedPreference.setCardName(PaymentActivity.this, cardName);
                        SaveSharedPreference.setExpDate(PaymentActivity.this, expDate);
                        SaveSharedPreference.setCheckSave(PaymentActivity.this, save);
                        SaveSharedPreference.setCardType(PaymentActivity.this,cardType);
                    } else {
                        SaveSharedPreference.clearData(PaymentActivity.this);
                    }
                    final String id = SaveSharedPreference.getID(PaymentActivity.this);
                    Calendar calendar = Calendar.getInstance();
                    final java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

                    String text = paymentPrice.getText().toString();
                    double price = Double.parseDouble(text.substring(2));
                    Payment payment = new Payment(currentTimestamp, price, id);
                    mDocumentReference = mFirebaseFirestore.document("Payment/" + paymentID);
                    mDocumentReference.set(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            pCollectionReference = mFirebaseFirestore.collection("Payment").document(paymentID).collection("PaymentDetail");

                            mCollectionReference = mFirebaseFirestore.collection("Customer/" + id + "/Cart");
                            mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Cart cart = documentSnapshot.toObject(Cart.class);
                                        String key = cart.getProductRef();
                                        int quantity = cart.getQuantity();
                                        amounts.add(quantity);
                                        mFirebaseFirestore.collection("Product").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Product product = documentSnapshot.toObject(Product.class);

                                                double price = Double.parseDouble(product.getPrice());
                                                if (product.getDiscount() != 0) {
                                                    double discountPercent = (100 - product.getDiscount()) * 0.01;
                                                    price = price * discountPercent;
                                                }
                                                items.add(product.getProductName());
                                                itemsprice.add(product.getPrice());
                                                PaymentDetail paymentDetail = new PaymentDetail(product.getProductName(), quantity, price);
                                                pCollectionReference.add(paymentDetail).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        createPDF();
                                                        return;
                                                    }
                                                });

                                                nDocumentReference = mFirebaseFirestore.collection("Product").document(key);
                                                int quantityStock = product.getStockAmount();
                                                quantityStock -= quantity;
                                                nDocumentReference.update("stockAmount", quantityStock).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        return;
                                                    }
                                                });

                                            }
                                        });


                                    }
                                    sendMail();
                                    deleteCollection();
                                    updatePromotion();
                                }
                            });


                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                            builder.setTitle("Payment Success");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean checkDate()
    {
        boolean check = true;
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sd.parse(expDates);
            Date currentDate = c.getTime();
            if(date.after(currentDate))
            {
                check = true;
            }else{
                check = false;
            }

        }catch (ParseException e)
        {
            e.printStackTrace();
        }
        return check;
    }

    private void updatePromotion(){
        String id = SaveSharedPreference.getID(PaymentActivity.this);
        String promoId = getIntent().getStringExtra("promoId");
        if(promoId!=null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            mFirebaseFirestore.collection("Promotion").document(promoId).collection("Customer").add(data);
        }
    }

    private void deleteCollection() {
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
                        return;
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
                Customer customer = documentSnapshot.toObject(Customer.class);
                String email = customer.getEmail();
                String subject = "Payment successfull ! This is your receipt";
                String message = "This your receipt, thanks for using the service";
                Log.i("Checksss",items.size()+"");
                /*for(int i = 0; i<items.size(); i++)
                {
                    Double amount = Double.parseDouble(itemsprice.get(i)) * amounts.get(i);
                    sb.append(items.get(i)+": "+amounts.get(i)+"  "+"RM"+amount+"\n");
                    message = sb.toString()+"\n"+"Total Payment: "+paymentPrice.getText().toString();
                }
                SendeMail sm = new SendeMail(PaymentActivity.this, email, subject, message);
                sm.execute();*/
                try {
                    SendeMail sm = new SendeMail(PaymentActivity.this, email, subject, message);
                    String file = Environment.getExternalStorageDirectory().getPath() + "/Payment.pdf";
                    Log.i("File ",file);
                    sm.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/Payment.pdf");
                    sm.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return;
    }

    public void createPDF(){
        Log.i("Check",items.size()+"");
        String saveLocation = Environment.getExternalStorageDirectory() + "/Payment.pdf";
        Log.i("File Location ", saveLocation);
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
            Chunk c = new Chunk("\n\n\nPayment Receipt\n", f);
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
            Log.i("Test", "Can it print");
            PdfPTable orderTable = new PdfPTable(new float[]{1f,5f,2f,2f});
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

            PdfPCell headerCell4 = new PdfPCell(new Phrase("Price",headerFont));
            headerCell4.setBackgroundColor(new BaseColor(220,220,220));
            headerCell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell4.setVerticalAlignment(Element.ALIGN_CENTER);
            headerCell4.setPaddingBottom(5f);
            headerCell4.setPaddingRight(10f);

            orderTable.setHeaderRows(1);
            orderTable.addCell(headerCell);
            orderTable.addCell(headerCell2);
            orderTable.addCell(headerCell3);
            orderTable.addCell(headerCell4);

            /*PdfPCell orderCell = new PdfPCell(new Phrase("1."));
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
            orderTable.addCell(orderCell3);*/
            int num = 1;
            for(int i=0; i<items.size();i++){
                Log.i("Testing", "Print");
                //View viewTemp = orderList.get(i);
                /*Spinner spinnerProductTemp = (Spinner) viewTemp.findViewById(R.id.spinner_product);
                EditText editTextQtyTemp = (EditText)viewTemp.findViewById(R.id.editTextQuantity);
                EditText editTextProductTemp = (EditText)viewTemp.findViewById(R.id.editTextProduct);
*/
                Double amount = Double.parseDouble(itemsprice.get(i)) * amounts.get(i);
                PdfPCell orderCell1 = new PdfPCell(new Phrase(num+"."));
                orderCell1.setPaddingLeft(10f);
                num++;

                PdfPCell orderCell2;
                orderCell2 = new PdfPCell(new Phrase(items.get(i),tableFont));
                orderCell2.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell2.setPaddingBottom(5f);
                orderCell2.setPaddingLeft(10f);

                PdfPCell orderCell3 = new PdfPCell(new Phrase(amounts.get(i)+"",tableFont));
                orderCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                orderCell3.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell3.setPaddingBottom(5f);
                orderCell3.setPaddingRight(10f);

                PdfPCell orderCell4 = new PdfPCell(new Phrase("RM "+String.format("%.2f",amount),tableFont));
                orderCell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                orderCell4.setVerticalAlignment(Element.ALIGN_CENTER);
                orderCell4.setPaddingBottom(5f);
                orderCell4.setPaddingRight(10f);

                orderTable.addCell(orderCell1);
                orderTable.addCell(orderCell2);
                orderTable.addCell(orderCell3);
                orderTable.addCell(orderCell4);
            }
            PdfPTable paymentTable = new PdfPTable(1);
            paymentTable.setTotalWidth(600f);
            String text = "Total Payment : "+paymentPrice.getText().toString();

            Font paymentFont = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.NORMAL, BaseColor.BLACK);
            PdfPCell paymentCell = new PdfPCell(new Phrase(text, paymentFont));
            paymentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            paymentCell.setBorder(Rectangle.NO_BORDER);

            paymentTable.addCell(paymentCell);

            doc.add(orderTable);
            doc.add(paymentTable);
            doc.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
