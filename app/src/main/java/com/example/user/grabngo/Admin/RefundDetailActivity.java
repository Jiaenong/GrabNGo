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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.Refund;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RefundDetailActivity extends AppCompatActivity {

    private ImageView imageViewRefund;
    private TextView textViewProduct, textViewCustomer, textViewModified, textViewDate, textViewReason;
    private ProgressDialog pDialog;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String documentID, imgURL;
    private CardView cardView1, cardView2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_detail);

        textViewProduct = (TextView)findViewById(R.id.text_view_product_name);
        textViewCustomer = (TextView)findViewById(R.id.text_view_customer);
        textViewModified = (TextView)findViewById(R.id.text_view_last_modified);
        textViewDate = (TextView)findViewById(R.id.text_view_date);
        textViewReason = (TextView)findViewById(R.id.text_view_reason);
        imageViewRefund = (ImageView)findViewById(R.id.product_image);
        btnEdit = (Button)findViewById(R.id.btn_edit);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        cardView1 = (CardView)findViewById(R.id.cardview1);
        cardView2 = (CardView)findViewById(R.id.cardview2);
        progressBar = (ProgressBar)findViewById(R.id.progressBarRefundDetail);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Refund");
        mFirebaseStorage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        documentID = intent.getStringExtra("refundID");

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RefundDetailActivity.this,EditRefundActivity.class);
                intent.putExtra("refundID",documentID);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RefundDetailActivity.this, R.style.AlertDialogCustom));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pDialog = new ProgressDialog(RefundDetailActivity.this);
                        pDialog.setMessage("Deleting...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        mCollectionReference.document(documentID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RefundDetailActivity.this, "Refund successfully deleted",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RefundDetailActivity.this, "Refund deletion unsuccessful",Toast.LENGTH_SHORT).show();
                                    }
                                });

                        mStorageReference = mFirebaseStorage.getReferenceFromUrl(imgURL);
                        mStorageReference.delete();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setTitle("Delete the refund?");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Refund Detail");

    }

    public void show(){
        mCollectionReference.document(documentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Refund refund = documentSnapshot.toObject(Refund.class);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                imgURL = refund.getImgUrl();

                Glide.with(RefundDetailActivity.this).load(refund.getImgUrl()).into(imageViewRefund);
                textViewProduct.setText(refund.getProductName());
                textViewModified.setText("Last modified by " + refund.getModifiedStaff() + ", " + df.format(refund.getModifiedDate()));
                textViewCustomer.setText("Name    : " + refund.getCustomerName());
                textViewDate.setText("Date      : " + df.format(refund.getRefundDate()));
                textViewReason.setText("Reason  : " + refund.getReason());
                cardView1.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);

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
