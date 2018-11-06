package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.Coupon;
import com.example.user.grabngo.Class.Discount;
import com.example.user.grabngo.Class.SelectProduct;
import com.example.user.grabngo.Class.DiscountProducts;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPromotionActivity extends AppCompatActivity {

    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_SELECT_PRODUCT = 3;

    private TextView textViewCoupon, textViewDiscount, textViewProduct;
    private EditText editTextTitle, editTextDescription, editTextStartDate, editTextEndDate, editTextCouponCode, editTextCashRebate;
    private Button btnSave, btnSelectProduct;
    private ImageView imageViewPromotion;
    private LinearLayout linearLayoutSelectProduct;
    private String type;
    private ProgressDialog pDialog;
    private TextInputLayout textInputLayout;
    private List<SelectProduct> promoList;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);
        AddPromotionActivity.this.setTitle("Add new promotion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewCoupon = (TextView)findViewById(R.id.textViewCoupon);
        textViewDiscount = (TextView)findViewById(R.id.textViewDiscount);
        textViewProduct = (TextView)findViewById(R.id.textViewProduct);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        editTextDescription = (EditText)findViewById(R.id.editTextDescription);
        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextCouponCode = (EditText)findViewById(R.id.editTextCode);
        editTextCashRebate = (EditText)findViewById(R.id.editTextRebate);
        imageViewPromotion = (ImageView)findViewById(R.id.imagePromotion);
        linearLayoutSelectProduct = (LinearLayout)findViewById(R.id.linearLayoutSelectProduct);
        btnSelectProduct = (Button)findViewById(R.id.btnSelectProduct);
        btnSave = (Button)findViewById(R.id.buttonSave);
        textInputLayout = (TextInputLayout)findViewById(R.id.textInputLayout);
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        textInputLayout.setHint("Coupon Code");
        type = "Coupon";

        textViewCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                type = "Coupon";
                textViewCoupon.setBackgroundColor(Color.WHITE);
                textViewCoupon.setTypeface(textViewCoupon.getTypeface(), Typeface.BOLD);
                textViewDiscount.setBackgroundResource(R.color.disable_button);
                textViewDiscount.setTypeface(null, Typeface.NORMAL);
                imageViewPromotion.setImageResource(R.drawable.discount_coupon);
                textInputLayout.setHint("Coupon Code");
                editTextCouponCode.setInputType(InputType.TYPE_CLASS_TEXT);
                linearLayoutSelectProduct.setVisibility(View.GONE);
                btnSelectProduct.setVisibility(View.VISIBLE);
                textViewProduct.setVisibility(View.GONE);
                editTextCashRebate.setVisibility(View.VISIBLE);

            }
        });

        textViewDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                type = "Product Discount";
                textViewDiscount.setBackgroundColor(Color.WHITE);
                textViewDiscount.setTypeface(textViewCoupon.getTypeface(), Typeface.BOLD);
                textViewCoupon.setBackgroundResource(R.color.disable_button);
                textViewCoupon.setTypeface(null, Typeface.NORMAL);
                imageViewPromotion.setImageResource(R.drawable.discount_product);
                textInputLayout.setHint("Discount Percentage");
                editTextCouponCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayoutSelectProduct.setVisibility(View.VISIBLE);
                editTextCashRebate.setVisibility(View.GONE);
            }
        });

        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstancePromotion(new Date(),1);

                dialog.show(manager,DIALOG_DATE);
            }
        });

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstancePromotion(new Date(),2);

                dialog.show(manager,DIALOG_DATE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coupon coupon = new Coupon();
                Discount discount = new Discount();

                if(type.equals("Coupon")){

                    if(!validateCoupon()){
                        return;
                    }

                    mFirebaseFirestore.collection("Promotion").whereEqualTo("type","Coupon").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            boolean check = true;
                            for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                Coupon couponTemp = documentSnapshot.toObject(Coupon.class);

                                if(editTextCouponCode.getText().toString().equals(couponTemp.getCode())){
                                    Toast.makeText(AddPromotionActivity.this, "Coupon code is already existed", Toast.LENGTH_SHORT).show();
                                    check = false;
                                    break;
                                }
                            }

                            if(check){
                                pDialog = new ProgressDialog(AddPromotionActivity.this);
                                pDialog.setMessage("Saving...");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                Date startDate=null, endDate=null;
                                try {
                                    startDate = df.parse(editTextStartDate.getText().toString());
                                    endDate = df.parse(editTextEndDate.getText().toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                coupon.setType(type);
                                coupon.setTitle(editTextTitle.getText().toString());
                                coupon.setDescription(editTextDescription.getText().toString());
                                coupon.setCode(editTextCouponCode.getText().toString());
                                coupon.setCashRebate(Integer.parseInt(editTextCashRebate.getText().toString()));
                                coupon.setStatus("ongoing");
                                coupon.setStartDate(startDate);
                                coupon.setEndDate(endDate);

                                FirebaseFirestore.getInstance().collection("Promotion").add(coupon).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddPromotionActivity.this, "Promotion successfully added",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                        finish();
                                    }
                                });


                            }
                        }
                    });


                }else{

                    if(!validateDiscount()){
                        return;
                    }

                    pDialog = new ProgressDialog(AddPromotionActivity.this);
                    pDialog.setMessage("Saving...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate=null, endDate=null;
                    try {
                        startDate = df.parse(editTextStartDate.getText().toString());
                        endDate = df.parse(editTextEndDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    discount.setType(type);
                    discount.setTitle(editTextTitle.getText().toString());
                    discount.setDescription(editTextDescription.getText().toString());
                    discount.setDiscount(Integer.parseInt(editTextCouponCode.getText().toString()));
                    discount.setStatus("ongoing");
                    discount.setStartDate(startDate);
                    discount.setEndDate(endDate);
                    FirebaseFirestore.getInstance().collection("Promotion").add(discount).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            for(int i=0; i<promoList.size(); i++){
                                DiscountProducts temp = new DiscountProducts();
                                temp.setName(promoList.get(i).getName());
                                temp.setRef(promoList.get(i).getDocumentId());
                                CollectionReference mCollectionReference = documentReference.collection("PromoProduct");
                                mCollectionReference.add(temp);
                            }

                        }
                    });

                    WriteBatch batch = mFirebaseFirestore.batch();
                    for(int i=0; i<promoList.size(); i++) {
                        DocumentReference documentRef = mFirebaseFirestore.collection("Product").document(promoList.get(i).getDocumentId());
                        batch.update(documentRef,"discount",Integer.parseInt(editTextCouponCode.getText().toString()));

                    }

                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddPromotionActivity.this, "Promotion successfully added",Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            finish();
                        }
                    });
                }


            }
        });

        btnSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPromotionActivity.this,SelectProductActivity.class);
                startActivityForResult(intent,REQUEST_SELECT_PRODUCT);
            }
        });

        textViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPromotionActivity.this,SelectProductActivity.class);
                startActivityForResult(intent,REQUEST_SELECT_PRODUCT);
            }
        });
    }

    public void clear(){
        editTextCouponCode.setText("");
        editTextStartDate.setText("");
        editTextDescription.setText("");
        editTextTitle.setText("");
        editTextEndDate.setText("");
        textViewProduct.setText("");
        editTextCashRebate.setText("");
    }

    public boolean validateCoupon(){
        boolean valid = false;

        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String startingDate = editTextStartDate.getText().toString();
        String endingDate = editTextEndDate.getText().toString();
        String code = editTextCouponCode.getText().toString();
        String cashRebate = editTextCashRebate.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null, endDate = null;

        try {
            startDate = dateFormat.parse(startingDate);
            endDate = dateFormat.parse(endingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        if(title.equals("")||description.equals("")||startingDate.equals("")||endingDate.equals("")||code.equals("")||cashRebate.equals("")){
            Toast.makeText(AddPromotionActivity.this, "All field cannot be empty", Toast.LENGTH_SHORT).show();
        }else if(startDate.before(cal.getTime())){
            Toast.makeText(AddPromotionActivity.this, "Starting date must be bigger than today", Toast.LENGTH_SHORT).show();
        }else if(startDate.after(endDate) || startDate.equals(endDate)){
            Toast.makeText(AddPromotionActivity.this, "Ending date must be bigger than starting date", Toast.LENGTH_SHORT).show();
        }else{
            valid=true;
        }

        return valid;
    }

    public boolean validateDiscount(){
        boolean valid = false;

        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String startingDate = editTextStartDate.getText().toString();
        String endingDate = editTextEndDate.getText().toString();
        String discount = editTextCouponCode.getText().toString();
        int discountPercent=0;
        if(!discount.equals("")){
            discountPercent = Integer.parseInt(discount);
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = null, endDate = null;

        try {
            startDate = dateFormat.parse(startingDate);
            endDate = dateFormat.parse(endingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        if(title.equals("")||description.equals("")||startingDate.equals("")||endingDate.equals("")||discount.equals("")){
            Toast.makeText(AddPromotionActivity.this, "All field cannot be empty", Toast.LENGTH_SHORT).show();
        }else if(textViewProduct.getText().toString().equals("")){
            Toast.makeText(AddPromotionActivity.this, "Please select the discount product", Toast.LENGTH_SHORT).show();
        }else if(discountPercent<=0 || discountPercent>=100){
            Toast.makeText(AddPromotionActivity.this, "Discount percent can only be in between 0 to 100(%)", Toast.LENGTH_SHORT).show();
        }else if(startDate.before(cal.getTime())){
            Toast.makeText(AddPromotionActivity.this, "Starting date must be bigger than today", Toast.LENGTH_SHORT).show();
        }else if(startDate.after(endDate) || startDate.equals(endDate)){
            Toast.makeText(AddPromotionActivity.this, "Ending date must be bigger than starting date", Toast.LENGTH_SHORT).show();
        }else{
            valid=true;
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SELECT_PRODUCT && resultCode == RESULT_OK){
            promoList = (List<SelectProduct>)data.getExtras().getSerializable("promoList");
            btnSelectProduct.setVisibility(View.GONE);
            textViewProduct.setVisibility(View.VISIBLE);

            String s = "";
            for(int i=0; i<promoList.size(); i++){
                int number = i+1;
                s += number + ". " + promoList.get(i).getName() + "\n";
            }

            textViewProduct.setText(s);
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
