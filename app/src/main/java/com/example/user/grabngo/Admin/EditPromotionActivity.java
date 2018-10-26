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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.Coupon;
import com.example.user.grabngo.Class.Discount;
import com.example.user.grabngo.Class.DiscountProducts;
import com.example.user.grabngo.Class.SelectProduct;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EditPromotionActivity extends AppCompatActivity {

    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_SELECT_PRODUCT = 3;

    private TextView textViewCoupon, textViewDiscount, textViewProduct;
    private EditText editTextTitle, editTextDescription, editTextStartDate, editTextEndDate, editTextCouponCode, editTextCashRebate;
    private Button btnSave, btnSelectProduct;
    private ImageView imageViewPromotion;
    private LinearLayout linearLayoutSelectProduct, linearLayoutAll;
    private String type, promotionId;
    private ProgressDialog pDialog;
    private TextInputLayout textInputLayout;
    private List<SelectProduct> promoList;
    private List<DiscountProducts> productsList;
    private FirebaseFirestore mFirebaseFirestore;
    private Discount discount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);
        EditPromotionActivity.this.setTitle("Edit promotion");
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
        linearLayoutAll = (LinearLayout)findViewById(R.id.linearLayoutAll);
        btnSelectProduct = (Button)findViewById(R.id.btnSelectProduct);
        btnSave = (Button)findViewById(R.id.buttonSave);
        textInputLayout = (TextInputLayout)findViewById(R.id.textInputLayout);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        promoList = new ArrayList<>();
        productsList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        linearLayoutAll.setVisibility(View.GONE);
        Intent intent = getIntent();
        type = intent.getStringExtra("promoType");
        promotionId = intent.getStringExtra("promotionId");

        if(type.equals("Coupon")){

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

            mFirebaseFirestore.collection("Promotion").document(promotionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Coupon coupon = documentSnapshot.toObject(Coupon.class);

                    editTextTitle.setText(coupon.getTitle());
                    editTextDescription.setText(coupon.getDescription());
                    editTextCouponCode.setText(coupon.getCode());
                    editTextCashRebate.setText(""+coupon.getCashRebate());

                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    editTextStartDate.setText(df.format(coupon.getStartDate()));
                    editTextEndDate.setText(df.format(coupon.getEndDate()));

                    progressBar.setVisibility(View.GONE);
                    linearLayoutAll.setVisibility(View.VISIBLE);
                }
            });
        }else{

            textViewDiscount.setBackgroundColor(Color.WHITE);
            textViewDiscount.setTypeface(textViewCoupon.getTypeface(), Typeface.BOLD);
            textViewCoupon.setBackgroundResource(R.color.disable_button);
            textViewCoupon.setTypeface(null, Typeface.NORMAL);
            imageViewPromotion.setImageResource(R.drawable.discount_product);
            textInputLayout.setHint("Discount Percentage");
            editTextCouponCode.setInputType(InputType.TYPE_CLASS_NUMBER);
            linearLayoutSelectProduct.setVisibility(View.VISIBLE);
            editTextCashRebate.setVisibility(View.GONE);
            btnSelectProduct.setVisibility(View.GONE);
            textViewProduct.setVisibility(View.VISIBLE);

            mFirebaseFirestore.collection("Promotion").document(promotionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    discount = documentSnapshot.toObject(Discount.class);

                    editTextTitle.setText(discount.getTitle());
                    editTextDescription.setText(discount.getDescription());
                    editTextCouponCode.setText(""+discount.getDiscount());

                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    editTextStartDate.setText(df.format(discount.getStartDate()));
                    editTextEndDate.setText(df.format(discount.getEndDate()));


                    CollectionReference collectionReference = documentSnapshot.getReference().collection("PromoProduct");
                    collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot1:queryDocumentSnapshots){
                                DiscountProducts discountProducts = documentSnapshot1.toObject(DiscountProducts.class);
                                discountProducts.setPromoRef(documentSnapshot1.getId());
                                productsList.add(discountProducts);
                            }

                            String s = "";
                            for(int i=0; i<productsList.size(); i++){
                                int number = i+1;
                                s += number + ". " + productsList.get(i).getName() + "\n";
                            }
                            textViewProduct.setText(s);
                            progressBar.setVisibility(View.GONE);
                            linearLayoutAll.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }

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

        textViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPromotionActivity.this,SelectProductActivity.class);
                intent.putExtra("promotionId",promotionId);
                startActivityForResult(intent,REQUEST_SELECT_PRODUCT);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate=null, endDate=null;
                try {
                    startDate = df.parse(editTextStartDate.getText().toString());
                    endDate = df.parse(editTextEndDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                pDialog = new ProgressDialog(EditPromotionActivity.this);
                pDialog.setMessage("Saving...");
                pDialog.setCancelable(false);
                pDialog.show();

                if(type.equals("Coupon")){

                    mFirebaseFirestore.collection("Promotion").document(promotionId).update("title",editTextTitle.getText().toString(),
                            "description",editTextDescription.getText().toString(),
                            "startDate",startDate,
                            "endDate", endDate,
                            "code", editTextCouponCode.getText().toString(),
                            "cashRebate",Integer.parseInt(editTextCashRebate.getText().toString()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditPromotionActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            finish();
                        }
                    });

                }else{

                    if(promoList.size()!=0){

                        WriteBatch batch = mFirebaseFirestore.batch();
                        for(int i=0; i<productsList.size(); i++){
                            DocumentReference mDocumentReference = mFirebaseFirestore.collection("Promotion").document(promotionId).collection("PromoProduct").document(productsList.get(i).getPromoRef());
                            batch.delete(mDocumentReference);
                        }

                        for(int i=0; i<productsList.size(); i++) {
                            DocumentReference documentRef = mFirebaseFirestore.collection("Product").document(productsList.get(i).getRef());
                            batch.update(documentRef,"discount",0);
                        }

                        for(int i=0; i<promoList.size(); i++) {
                            DocumentReference documentRef = mFirebaseFirestore.collection("Product").document(promoList.get(i).getDocumentId());
                            batch.update(documentRef,"discount",Integer.parseInt(editTextCouponCode.getText().toString()));
                        }

                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                productsList.clear();

                                for(int i=0; i<promoList.size(); i++){
                                    DiscountProducts temp = new DiscountProducts();
                                    temp.setName(promoList.get(i).getName());
                                    temp.setRef(promoList.get(i).getDocumentId());
                                    productsList.add(temp);
                                }

                                for(int j=0; j<productsList.size(); j++){
                                    CollectionReference mCollectionReference = mFirebaseFirestore.collection("Promotion").document(promotionId).collection("PromoProduct");
                                    mCollectionReference.add(productsList.get(j));
                                }

                            }
                        });


                    }else {
                        WriteBatch batch = mFirebaseFirestore.batch();
                        for(int i=0; i<productsList.size(); i++) {
                            DocumentReference documentRef = mFirebaseFirestore.collection("Product").document(productsList.get(i).getRef());
                            batch.update(documentRef,"discount",Integer.parseInt(editTextCouponCode.getText().toString()));
                        }
                        batch.commit();
                    }

                    mFirebaseFirestore.collection("Promotion").document(promotionId).update("title",editTextTitle.getText().toString(),
                            "description",editTextDescription.getText().toString(),
                            "startDate",startDate,
                            "endDate", endDate,
                            "discount",Integer.parseInt(editTextCouponCode.getText().toString()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditPromotionActivity.this, "Changes has been saved", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            finish();
                        }
                    });

                }

            }
        });
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
