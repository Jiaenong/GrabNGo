package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.grabngo.Class.Coupon;
import com.example.user.grabngo.Class.Discount;
import com.example.user.grabngo.Class.DiscountProducts;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DiscountDetailActivity extends AppCompatActivity {

    private TextView couponTitle, couponDescription, couponType, couponDuration, couponCode, couponRebate, couponStatus,
                     discountTitle, discountDescription, discountType, discountDuration, discountPercent, discountProduct, discountStatus;
    private CardView cardViewCoupon, cardViewDiscount;
    private Button btnDelete, btnEdit;
    private String promotionId, type;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private LinearLayout linearLayout;
    private Discount discount;
    private List<DiscountProducts> productsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_detail);

        couponTitle = (TextView) findViewById(R.id.textCouponTitle);
        couponDescription = (TextView) findViewById(R.id.textCouponDescription);
        couponType = (TextView) findViewById(R.id.textCouponType);
        couponDuration = (TextView) findViewById(R.id.textCouponDuration);
        couponCode = (TextView) findViewById(R.id.textCouponCoupon);
        couponRebate = (TextView)findViewById(R.id.textCouponRebate);
        couponStatus = (TextView) findViewById(R.id.textCouponStatus);
        discountTitle = (TextView) findViewById(R.id.textDiscountTitle);
        discountDescription = (TextView) findViewById(R.id.textDiscountDescription);
        discountType = (TextView) findViewById(R.id.textDiscountType);
        discountDuration = (TextView) findViewById(R.id.textDiscountDuration);
        discountPercent  = (TextView) findViewById(R.id.textDiscountPercent);
        discountProduct = (TextView)findViewById(R.id.textDiscountProduct);
        discountStatus = (TextView) findViewById(R.id.textDiscountStatus);
        cardViewCoupon = (CardView)findViewById(R.id.cardViewCoupon);
        cardViewDiscount = (CardView)findViewById(R.id.cardViewDiscount);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        btnEdit = (Button)findViewById(R.id.btn_edit);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Promotion");
        productsList = new ArrayList<>();

        linearLayout.setVisibility(View.GONE);
        cardViewCoupon.setVisibility(View.GONE);
        cardViewDiscount.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        promotionId = intent.getStringExtra("promotionId");
        type = intent.getStringExtra("promoType");

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }*/

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DiscountDetailActivity.this, R.style.AlertDialogCustom));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pDialog = new ProgressDialog(DiscountDetailActivity.this);
                        pDialog.setMessage("Deleting...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        if(type.equals("Product Discount")){
                            WriteBatch batch = mFirebaseFirestore.batch();

                            for(int j=0; j<productsList.size(); j++) {
                                DocumentReference documentRef = mFirebaseFirestore.collection("Product").document(productsList.get(j).getRef());
                                batch.update(documentRef,"discount",0);

                                DocumentReference documentPromoRef = mFirebaseFirestore.collection("Promotion").document(promotionId).collection("PromoProduct").document(productsList.get(j).getPromoRef());
                                batch.delete(documentPromoRef);
                            }

                            batch.commit();
                        }

                        mCollectionReference.document(promotionId).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DiscountDetailActivity.this, "Promotion successfully deleted",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DiscountDetailActivity.this, "Promotion deletion unsuccessful",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setTitle("Delete the promotion?");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit = new Intent(DiscountDetailActivity.this,EditPromotionActivity.class);
                intentEdit.putExtra("promotionId",promotionId);
                intentEdit.putExtra("promoType",type);
                startActivity(intentEdit);
            }
        });

        setTitle("Promotion Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void show(){
        mCollectionReference.document(promotionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if(type.equals("Coupon")) {
                    Coupon coupon = documentSnapshot.toObject(Coupon.class);

                    couponTitle.setText(coupon.getTitle());
                    couponDescription.setText(coupon.getDescription());
                    couponType.setText("Promo Type  : " + coupon.getType());
                    couponCode.setText("Coupon code : " + coupon.getCode());
                    couponRebate.setText("Cash Rebate  : RM " + String.format("%.2f",(double)coupon.getCashRebate()));
                    couponStatus.setText("Status             : " + coupon.getStatus());
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    couponDuration.setText("Duration         : " + dateFormat.format(coupon.getStartDate()) + " - " + dateFormat.format(coupon.getEndDate()));

                    cardViewCoupon.setVisibility(View.VISIBLE);
                    cardViewDiscount.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }else{

                    discount = documentSnapshot.toObject(Discount.class);

                    mCollectionReference.document(promotionId).collection("PromoProduct").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot1:queryDocumentSnapshots){
                                DiscountProducts discountProducts = documentSnapshot1.toObject(DiscountProducts.class);
                                discountProducts.setPromoRef(documentSnapshot1.getId());
                                productsList.add(discountProducts);
                            }

                            String products = "";
                            for(int j=0; j<productsList.size(); j++) {
                                int number = j+1;
                                products += number + ". " + productsList.get(j).getName() + "\n";
                            }
                            discountProduct.setText(products);
                            discountTitle.setText(discount.getTitle());
                            discountDescription.setText(discount.getDescription());
                            discountType.setText("Promo Type  : " + discount.getType());
                            discountPercent.setText("Discount        : " + discount.getDiscount() + " %");
                            discountStatus.setText("Status             : " + discount.getStatus());
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            discountDuration.setText("Duration         : " + dateFormat.format(discount.getStartDate()) + " - " + dateFormat.format(discount.getEndDate()));

                            cardViewCoupon.setVisibility(View.GONE);
                            cardViewDiscount.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            btnEdit.setVisibility(View.VISIBLE);
                            btnDelete.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }



            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        productsList.clear();
        linearLayout.setVisibility(View.GONE);
        cardViewCoupon.setVisibility(View.GONE);
        cardViewDiscount.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        show();
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
}
