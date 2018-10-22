package com.example.user.grabngo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.grabngo.Class.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ViewDialog {

    private List<Product> productList;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Activity activity;
    private LowStockAdapter lowStockAdapter;

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_low_stock_alert);

        this.activity = activity;
        productList = new ArrayList<>();
        Button dialogButton = (Button)dialog.findViewById(R.id.btn_ok);
        recyclerView = (RecyclerView)dialog.findViewById(R.id.recycleViewLowStock);
        progressBar = (ProgressBar)dialog.findViewById(R.id.progressBar);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Product");

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void show(){
        productList.clear();

        mCollectionReference.whereLessThanOrEqualTo("stockAmount",100).whereEqualTo("lowStockAlert",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product = documentSnapshot.toObject(Product.class);
                    String image = product.getImageUrl();
                    String name = product.getProductName();
                    int stockAmount = product.getStockAmount();
                    Product mProduct = new Product(image, name, stockAmount, documentSnapshot.getId());
                    productList.add(mProduct);
                }
                lowStockAdapter = new LowStockAdapter(activity,productList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(lowStockAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }


public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;

    @Override
    public LowStockAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lowstockalert,parent,false);
        return new LowStockAdapter.MyViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(LowStockAdapter.MyViewHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.name.setText(product.getProductName());
        holder.stockAmount.setText("" + product.getStockAmount());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, stockAmount;
        public ImageView imageViewProduct;
        public Button btnIgnore, btnReorder;


        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView)view.findViewById(R.id.textViewProductName);
            stockAmount = (TextView)view.findViewById(R.id.textViewProductQuantity);

        }
    }

    public LowStockAdapter(Context mContext, List<Product> productList)
    {
        this.mContext = mContext;
        this.productList = productList;
    }

}

}

