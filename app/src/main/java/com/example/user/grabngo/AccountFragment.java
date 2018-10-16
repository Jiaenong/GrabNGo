package com.example.user.grabngo;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ImageButton btnPurchaseHistory, btnEditProfile, btnForgetPassword, btnLogout;
    private TextView textViewName, textViewGender, textViewEmail, textViewAddress;
    private ProgressBar progressBarAccount;
    private LinearLayout linearLayoutAccount;

    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_account);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        setHasOptionsMenu(true);
        String id = SaveSharedPreference.getID(getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Customer/"+id);
        textViewName = (TextView)v.findViewById(R.id.textViewName);
        textViewGender = (TextView)v.findViewById(R.id.textViewGender);
        textViewEmail = (TextView)v.findViewById(R.id.textViewEmail);
        textViewAddress = (TextView)v.findViewById(R.id.textViewAddress);
        btnPurchaseHistory = (ImageButton)v.findViewById(R.id.btn_purchase_history);
        btnEditProfile = (ImageButton)v.findViewById(R.id.btn_edit_profile);
        btnForgetPassword = (ImageButton)v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton)v.findViewById(R.id.btn_logout);
        progressBarAccount = (ProgressBar)v.findViewById(R.id.progressBarAccount);
        linearLayoutAccount = (LinearLayout)v.findViewById(R.id.linearLayoutAccount);

        final FragmentManager fm = getFragmentManager();

        progressBarAccount.setVisibility(View.VISIBLE);
        linearLayoutAccount.setVisibility(View.GONE);

        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Customer customer = documentSnapshot.toObject(Customer.class);
                textViewName.setText("Name: "+customer.getName());
                textViewGender.setText("Gender: "+customer.getAddress());
                textViewEmail.setText("Email: "+customer.getEmail());
                textViewAddress.setText("Address: "+customer.getAddress());
                progressBarAccount.setVisibility(View.GONE);
                linearLayoutAccount.setVisibility(View.VISIBLE);
            }
        });

        btnPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PurchaseHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment editProfileFragment = new EditProfileFragment();
                fm.beginTransaction().replace(R.id.fragment_container,editProfileFragment).commit();
            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment forgetPasswordFragment = new ForgetPasswordFragment();
                fm.beginTransaction().replace(R.id.fragment_container,forgetPasswordFragment).commit();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSharedPreference.clearUser(getContext());
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(getActivity(),CartActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return;
    }

}
