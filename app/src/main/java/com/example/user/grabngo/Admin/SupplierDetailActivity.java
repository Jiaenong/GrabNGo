package com.example.user.grabngo.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.user.grabngo.Class.Supplier;
import com.example.user.grabngo.ProductDetailActivity;
import com.example.user.grabngo.R;
import com.example.user.grabngo.RecyclerTouchListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SupplierDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewPhone, textViewEmail, textViewLocation, textViewEmpty;
    private ImageView imageViewSupplier;
    private ProgressDialog pDialog;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, mCollectionReference2;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private ProgressBar progressBar;
    private CardView cardView1, cardView2;
    private String selectedSupplier, imgURL;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private SupplierProductAdapter supplierProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Supplier Detail");

        productList = new ArrayList<>();
        textViewName = (TextView)findViewById(R.id.textViewName);
        textViewEmail = (TextView)findViewById(R.id.textViewEmail);
        textViewPhone = (TextView)findViewById(R.id.textViewPhone);
        textViewEmpty = (TextView)findViewById(R.id.textViewEmpty);
        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        recyclerView = (RecyclerView)findViewById(R.id.recycleViewProduct);
        imageViewSupplier = (ImageView)findViewById(R.id.imageViewSupplier);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
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
        mCollectionReference = mFirebaseFirestore.collection("Supplier");
        mCollectionReference2 = mFirebaseFirestore.collection("Product");
        mFirebaseStorage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        selectedSupplier = intent.getStringExtra("supplierID");

        supplierProductAdapter = new SupplierProductAdapter(SupplierDetailActivity.this, productList);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(SupplierDetailActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(SupplierDetailActivity.this,ProductDetailActivity.class);
                intent.putExtra("Activity",true);
                intent.putExtra("productName",product.getProductName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SupplierDetailActivity.this, R.style.AlertDialogCustom));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pDialog = new ProgressDialog(SupplierDetailActivity.this);
                        pDialog.setMessage("Deleting...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        mCollectionReference.document(selectedSupplier).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SupplierDetailActivity.this, "Supplier successfully deleted",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SupplierDetailActivity.this, "Supplier deletion unsuccessful",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(SupplierDetailActivity.this, AddSupplierActivity.class);
                intent.putExtra("supplierID",selectedSupplier);
                startActivity(intent);
            }
        });
    }

    private void showProductDetail() {
        mCollectionReference.document(selectedSupplier).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Supplier supplier = documentSnapshot.toObject(Supplier.class);
                imgURL = supplier.getPicUrl();

                textViewName.setText("Name       : "+supplier.getName());
                textViewEmail.setText("Email       : " + supplier.getEmail());
                textViewPhone.setText("H/P          : " + supplier.getPhone());
                textViewLocation.setText("Location  : " + supplier.getLocation());
                Glide.with(SupplierDetailActivity.this).load(supplier.getPicUrl()).into(imageViewSupplier);
                cardView1.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void showProduct(){
        productList.clear();
        mCollectionReference2.whereEqualTo("supplierKey",selectedSupplier).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product = documentSnapshot.toObject(Product.class);
                    Product mProduct = new Product(product.getImageUrl(), product.getProductName(), product.getStockAmount(), documentSnapshot.getId());
                    productList.add(mProduct);
                }

                if(productList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }else{
                    supplierProductAdapter = new SupplierProductAdapter(SupplierDetailActivity.this,productList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SupplierDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(supplierProductAdapter);
                }

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
        showProduct();
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
