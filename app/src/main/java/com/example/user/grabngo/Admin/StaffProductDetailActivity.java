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
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffProductDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewModified, textViewPrice, textViewCategory, textViewProducer, textViewAmount, textViewLocation, textViewExpired;
    private ImageView imageViewProduct;
    private ProgressDialog pDialog;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DocumentReference mDocumentReference;
    private ProgressBar progressBar, progressBarDelete;
    private CardView cardView1, cardView2;
    private String imgURL, selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_product_detail);

        textViewName = (TextView)findViewById(R.id.text_view_product_name);
        textViewModified = (TextView)findViewById(R.id.text_view_last_modified);
        textViewPrice = (TextView)findViewById(R.id.text_view_price);
        textViewCategory = (TextView)findViewById(R.id.text_view_category);
        textViewProducer = (TextView)findViewById(R.id.text_view_producer);
        textViewAmount = (TextView)findViewById(R.id.text_view_stock);
        textViewLocation = (TextView)findViewById(R.id.text_view_location);
        textViewExpired = (TextView)findViewById(R.id.text_view_expired);
        imageViewProduct = (ImageView)findViewById(R.id.product_image);
        progressBar = (ProgressBar)findViewById(R.id.progressBarProductDetail);
        cardView1 = (CardView)findViewById(R.id.cardview1);
        cardView2 = (CardView)findViewById(R.id.cardview2);
        btnEdit = (Button)findViewById(R.id.btn_edit);
        btnDelete = (Button)findViewById(R.id.btn_delete);

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");
        mFirebaseStorage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        selectedProduct = intent.getStringExtra("productID");

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(StaffProductDetailActivity.this, R.style.AlertDialogCustom));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pDialog = new ProgressDialog(StaffProductDetailActivity.this);
                        pDialog.setMessage("Deleting...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        mCollectionReference.document(selectedProduct).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(StaffProductDetailActivity.this, "Product successfully deleted",Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StaffProductDetailActivity.this, "Product deletion unsuccessful",Toast.LENGTH_SHORT).show();
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
                builder.setTitle("Delete the product?");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffProductDetailActivity.this,EditProductActivity.class);
                intent.putExtra("productID",selectedProduct);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Product Detail");
    }

    private void showProductDetail() {
        mCollectionReference.document(selectedProduct).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                imgURL = product.getImageUrl();

                textViewName.setText(product.getProductName());
                textViewModified.setText("Last Modified by " + product.getModifiedStaffName() + ", " + df.format(product.getModifiedDate()));
                textViewPrice.setText("Price                     : RM " + product.getPrice());
                textViewProducer.setText("Producer              : " + product.getProducer());
                textViewCategory.setText("Category              : " + product.getCategory());
                textViewExpired.setText("Expired date       : " + product.getExpired());
                textViewAmount.setText("Stock amount      : " + product.getStockAmount());
                textViewLocation.setText("Shelf location      : " + product.getShelfLocation());
                Glide.with(StaffProductDetailActivity.this).load(product.getImageUrl()).into(imageViewProduct);
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

        showProductDetail();
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
