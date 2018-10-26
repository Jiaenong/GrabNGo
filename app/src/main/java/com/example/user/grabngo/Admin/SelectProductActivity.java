package com.example.user.grabngo.Admin;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.DiscountProducts;
import com.example.user.grabngo.Class.Product;
import com.example.user.grabngo.Class.SelectProduct;
import com.example.user.grabngo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button btnConfirm;
    private SelectProductAdapter selectProductAdapter;
    private List<SelectProduct> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        recyclerView = (RecyclerView)findViewById(R.id.recycleViewProduct);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnConfirm = (Button)findViewById(R.id.btn_confirm);
        productList = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SelectProductActivity.this.setTitle("Select Product");

        Intent intent = getIntent();
        String promotionId = intent.getStringExtra("promotionId");

        if(promotionId != null){

            FirebaseFirestore.getInstance().collection("Promotion").document(promotionId).collection("PromoProduct").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        DiscountProducts discountProducts = documentSnapshot.toObject(DiscountProducts.class);

                        FirebaseFirestore.getInstance().collection("Product").document(discountProducts.getRef()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Product product = documentSnapshot.toObject(Product.class);

                                SelectProduct selectProduct = new SelectProduct(product.getProductName(),
                                        documentSnapshot.getId(),
                                        product.getPrice(),
                                        product.getImageUrl(),
                                        product.getStockAmount());
                                productList.add(selectProduct);

                            }
                        });

                    }

                    setProduct();
                }
            });
        }else {
            setProduct();
        }



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SelectProduct> promoList = selectProductAdapter.getPromoList();

                if(promoList.size()==0) {
                    Toast.makeText(SelectProductActivity.this,"Please select a product", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("promoList", (Serializable) promoList);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    public void setProduct(){
        FirebaseFirestore.getInstance().collection("Product").orderBy("productName").whereEqualTo("discount",0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Product product = documentSnapshot.toObject(Product.class);
                    SelectProduct selectProduct = new SelectProduct(product.getProductName(),
                            documentSnapshot.getId(),
                            product.getPrice(),
                            product.getImageUrl(),
                            product.getStockAmount());

                    productList.add(selectProduct);
                }

                selectProductAdapter = new SelectProductAdapter(productList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectProductActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(selectProductAdapter);
                progressBar.setVisibility(View.GONE);
            }
        });
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

    public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.MyViewHolder> {

        private List<SelectProduct> productList;
        List<SelectProduct> promoList = new ArrayList<>();

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView product, price, quantity;
            public ImageView productImage;
            public ImageButton imageButton;
            public CheckBox checkBox;

            public MyViewHolder(View view) {
                super(view);
                price = (TextView) view.findViewById(R.id.product_price);
                product = (TextView) view.findViewById(R.id.product_name);
                quantity = (TextView) view.findViewById(R.id.product_quantity);
                productImage = (ImageView)view.findViewById(R.id.image_product);
                imageButton = (ImageButton)view.findViewById(R.id.button_delete);
                checkBox = (CheckBox)view.findViewById(R.id.checkBoxPromo);
            }
        }

        public SelectProductAdapter(List<SelectProduct> productList){

            this.productList = productList;
        }

        public List<SelectProduct> getPromoList(){
            return promoList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final SelectProduct selectProduct = productList.get(position);
            holder.imageButton.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.product.setText(selectProduct.getName());
            holder.price.setText("RM "+String.format("%.2f",Double.parseDouble(selectProduct.getPrice())));
            holder.quantity.setText("Quantity: "+selectProduct.getQuantity());
            Glide.with(SelectProductActivity.this).load(selectProduct.getImgUrl()).into(holder.productImage);


            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        promoList.add(selectProduct);
                    }else{
                        promoList.remove(selectProduct);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }
    }
}
