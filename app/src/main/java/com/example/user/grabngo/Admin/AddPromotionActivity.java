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
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                Task<DocumentReference> tf = null;

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

                if(type.equals("Coupon")){

                    coupon.setType(type);
                    coupon.setTitle(editTextTitle.getText().toString());
                    coupon.setDescription(editTextDescription.getText().toString());
                    coupon.setCode(editTextCouponCode.getText().toString());
                    coupon.setCashRebate(Integer.parseInt(editTextCashRebate.getText().toString()));
                    coupon.setStatus("ongoing");
                    coupon.setStartDate(startDate);
                    coupon.setEndDate(endDate);

                    tf = FirebaseFirestore.getInstance().collection("Promotion").add(coupon);
                    tf.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddPromotionActivity.this, "Promotion successfully added",Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            finish();
                        }
                    });

                }else{

                    discount.setType(type);
                    discount.setTitle(editTextTitle.getText().toString());
                    discount.setDescription(editTextDescription.getText().toString());
                    discount.setDiscount(Integer.parseInt(editTextCouponCode.getText().toString()));
                    discount.setStatus("ongoing");
                    discount.setStartDate(startDate);
                    discount.setEndDate(endDate);
                    tf = FirebaseFirestore.getInstance().collection("Promotion").add(discount);
                    tf.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
