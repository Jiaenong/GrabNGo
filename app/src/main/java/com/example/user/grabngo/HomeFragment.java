package com.example.user.grabngo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.grabngo.Class.PaymentDetail;
import com.example.user.grabngo.Class.PaymentList;
import com.example.user.grabngo.Class.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewRecommend;
    private RecyclerView recyclerViewNew;
    private ProgressBar progressBarProductHome;
    private TextView textViewRecommendMore, textViewNewMore;
    private ScrollView scrollViewHome;
    List<String> myProductList;
    List<Product>productList;
    List<Product> newProduct;
    List<PaymentList> pList;
    private RecommendAdapter adapter;
    private PaymentList paymentList1;
    private int check = 0;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference, nCollectionReference, pCollectionReference, oCollectionReference, qCollectionReference;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewRecommend = (RecyclerView)v.findViewById(R.id.recycleViewRecommend);
        recyclerViewNew = (RecyclerView)v.findViewById(R.id.recycleViewNew);
        progressBarProductHome = (ProgressBar)v.findViewById(R.id.progressBarProductHome);
        scrollViewHome = (ScrollView)v.findViewById(R.id.scrollViewHome);
        textViewNewMore = (TextView)v.findViewById(R.id.textViewNewMore);
        textViewRecommendMore = (TextView)v.findViewById(R.id.textViewRecommendMore);
        progressBarProductHome.setVisibility(View.VISIBLE);
        scrollViewHome.setVisibility(View.GONE);

        pList = new ArrayList<>();

        textViewNewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RecommendActivity.class);
                intent.putExtra("myList",(Serializable)newProduct);
                intent.putExtra("check","New");
                startActivity(intent);

            }
        });
        textViewRecommendMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecommendActivity.class);
                intent.putExtra("myList", (Serializable)productList);
                intent.putExtra("check","Recommend");
                startActivity(intent);
            }
        });
        recyclerViewRecommend.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewRecommend, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product products = productList.get(position);
                Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
                intent.putExtra("productName",products.getProductName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerViewNew.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewNew, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product productss = newProduct.get(position);
                Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
                intent.putExtra("productName",productss.getProductName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
       readData(new MyCallBack() {
           @Override
           public void onCallBack(List<PaymentList> myList) {
               Log.i("myList Size ", myList.size()+"");
               myProductList = new ArrayList<>();
               int numss = myList.get(0).getProductNum();
               for(int i = 0; i<myList.size();i++)
               {
                   if(myList.get(i).getProductNum()>numss);
                   {
                       numss = myList.get(i).getProductNum();
                       myProductList.add(myList.get(i).getProductName());
                       if(myProductList.size() == 5)
                       {
                           break;
                       }
                   }
               }
                   readSecondData(new MySecondCallBack() {
                       @Override
                       public void onSecondCallBack(List<Product> mySecondList) {
                           productList = new ArrayList<>();
                           for(int i = 0; i< mySecondList.size(); i++)
                           {
                               productList.add(mySecondList.get(i));
                           }
                           adapter = new RecommendAdapter(productList);
                           RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                           recyclerViewRecommend.setLayoutManager(mLayoutManager);
                           recyclerViewRecommend.setItemAnimator(new DefaultItemAnimator());
                           recyclerViewRecommend.setAdapter(adapter);
                           progressBarProductHome.setVisibility(View.GONE);
                           scrollViewHome.setVisibility(View.VISIBLE);
                           //Log.i("Product Size ",mySecondList.size()+"");
                       }
                   });
               }

       });

       getNewItem();

        setHasOptionsMenu(true);
        return v;
    }

    private void getNewItem() {
        mCollectionReference = mFirebaseFirestore.collection("Product");
        mCollectionReference.orderBy("modifiedDate",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                newProduct = new ArrayList<>();
                int i = 0;
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Product product3 = documentSnapshot.toObject(Product.class);
                    newProduct.add(product3);
                    i++;
                    if(i == 5)
                    {
                        break;
                    }
                }
                adapter = new RecommendAdapter(newProduct);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                recyclerViewNew.setLayoutManager(mLayoutManager);
                recyclerViewNew.setItemAnimator(new DefaultItemAnimator());
                recyclerViewNew.setAdapter(adapter);
            }
        });
    }


    public interface  MySecondCallBack{
        void onSecondCallBack(List<Product> mySecondList);
    }

    public void readSecondData(MySecondCallBack mySecondCallBack)
    {
        pCollectionReference = mFirebaseFirestore.collection("Product");
        List<Product> mySecondList = new ArrayList<>();
        for(int i = 0; i<myProductList.size(); i++)
        {
            pCollectionReference.whereEqualTo("productName",myProductList.get(i)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        Product product = documentSnapshot.toObject(Product.class);
                        mySecondList.add(product);
                    }
                    mySecondCallBack.onSecondCallBack(mySecondList);
                }
            });
        }

    }

    public interface MyCallBack{
        void onCallBack(List<PaymentList> myList);
    }

    public void readData(MyCallBack myCallBack)
    {
        String id = SaveSharedPreference.getID(getActivity());
        mCollectionReference = mFirebaseFirestore.collection("Payment");
        mCollectionReference.whereEqualTo("customerKey",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean stop = false;
                String key = "";
                List<PaymentList> myList = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Log.i("Test ", "Hello");
                    check++;
                    key = documentSnapshot.getId();
                    nCollectionReference = mFirebaseFirestore.collection("Payment").document(key).collection("PaymentDetail");
                    nCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int quantity = 1;
                            int numss = 0;
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                int number = 0;
                                boolean check = true;
                                PaymentDetail paymentDetail = documentSnapshot.toObject(PaymentDetail.class);
                                String name = paymentDetail.getProductName();
                                //Log.i("Name ",name);
                                if(myList.isEmpty())
                                {
                                    PaymentList paymentList = new PaymentList(name, quantity);
                                    myList.add(paymentList);
                                }else{
                                    for (int i = 0; i < myList.size(); i++) {
                                        if (name.equals(myList.get(i).getProductName())) {
                                            //Log.i("Equal ",myList.get(i).getProductName());
                                            int qty = myList.get(i).getProductNum();
                                            qty++;
                                            myList.get(i).setProductNum(qty);
                                            check = false;
                                            break;
                                        }else{
                                            //Log.i("Not Equal ",myList.get(i).getProductName());
                                            int num = 1;
                                            paymentList1 = new PaymentList(name, num);
                                        }
                                    }
                                    if(check == true)
                                    {
                                        myList.add(paymentList1);
                                    }
                                }

                            }
                            myCallBack.onCallBack(myList);
                        }
                    });
                }
                if(check == 0)
                {
                    Log.i("testinh ", "Ooi");
                    oCollectionReference = mFirebaseFirestore.collection("Payment");
                    oCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                String id = documentSnapshot.getId();
                                qCollectionReference = mFirebaseFirestore.collection("Payment").document(id).collection("PaymentDetail");
                                qCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        int quantity = 1;
                                        int numss = 0;
                                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                        {
                                            int number = 0;
                                            boolean check = true;
                                            PaymentDetail paymentDetail = documentSnapshot.toObject(PaymentDetail.class);
                                            String name = paymentDetail.getProductName();
                                            //Log.i("Name ",name);
                                            if(myList.isEmpty())
                                            {
                                                PaymentList paymentList = new PaymentList(name, quantity);
                                                myList.add(paymentList);
                                            }else{
                                                for (int i = 0; i < myList.size(); i++) {
                                                    if (name.equals(myList.get(i).getProductName())) {
                                                        //Log.i("Equal ",myList.get(i).getProductName());
                                                        int qty = myList.get(i).getProductNum();
                                                        qty++;
                                                        myList.get(i).setProductNum(qty);
                                                        check = false;
                                                        break;
                                                    }else{
                                                        //Log.i("Not Equal ",myList.get(i).getProductName());
                                                        int num = 1;
                                                        paymentList1 = new PaymentList(name, num);
                                                    }
                                                }
                                                if(check == true)
                                                {
                                                    myList.add(paymentList1);
                                                }
                                            }

                                        }
                                        myCallBack.onCallBack(myList);
                                    }
                                });
                            }
                        }
                    });
                }
                //myCallBack.onCallBack(myList);
            }
        });
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            getActivity().invalidateOptionsMenu();
            MenuItem itemCart = menu.findItem(R.id.cart);
            LayerDrawable icon = (LayerDrawable)itemCart.getIcon();
            setBadgeCount(getActivity(),icon, SaveSharedPreference.getCartNumber(getActivity())+"");
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(getActivity(),CartActivity.class));
                return true;
            case R.id.action_search:
                startActivity(new Intent(getActivity(),SearchActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart,menu);

        return;
    }

    public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.MyViewHolder>{
        private List<Product> listProduct;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View recommendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_view, parent, false);
            return new MyViewHolder(recommendView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Product product = listProduct.get(position);
            holder.textViewProductName.setText(product.getProductName());
            holder.txtViewOutOfStock.setVisibility(View.GONE);
            if(product.getStockAmount() == 0)
            {
                holder.txtViewOutOfStock.setVisibility(View.VISIBLE);
                holder.textViewOriPrice.setVisibility(View.GONE);
                holder.textViewProductPrice.setVisibility(View.GONE);
            }
            if(product.getDiscount()!=0){
                holder.textViewOriPrice.setVisibility(View.VISIBLE);
                holder.textViewOriPrice.setText("RM " + product.getPrice());
                holder.textViewOriPrice.setPaintFlags(holder.textViewOriPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                double price = Double.parseDouble(product.getPrice());
                double discountPercent = (100 - product.getDiscount())*0.01;
                price = price * discountPercent;
                holder.textViewProductPrice.setText("RM " + String.format("%.2f",price));
            }else{
                holder.textViewProductPrice.setText("RM " + product.getPrice());
            }
            Glide.with(getActivity()).load(product.getImageUrl()).into(holder.imageViewProductImage);
        }

        @Override
        public int getItemCount() {
            return listProduct.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewProductName, textViewProductPrice, textViewOriPrice, txtViewOutOfStock;
            public ImageView imageViewProductImage;

            public MyViewHolder(View view)
            {
                super(view);
                textViewProductName = (TextView)view.findViewById(R.id.textViewProductName);
                textViewProductPrice = (TextView)view.findViewById(R.id.textViewProductPrice);
                imageViewProductImage = (ImageView)view.findViewById(R.id.imageViewProductImage);
                textViewOriPrice = (TextView)view.findViewById(R.id.textViewProductOriPrice);
                txtViewOutOfStock = (TextView)view.findViewById(R.id.txtViewOutOfStock);
            }
        }

        public RecommendAdapter(List<Product> lp)
        {
            listProduct = lp;
        }
    }

}
