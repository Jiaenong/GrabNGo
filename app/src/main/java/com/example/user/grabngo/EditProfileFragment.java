package com.example.user.grabngo;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private Button btnSave, btnCancel;
    private ImageButton btnPurchaseHistory, btnEditProfile, btnForgetPassword, btnLogout;
    private EditText editTextName, editTextEmail, editTextAddress;
    private RadioButton radioButtonMale, radioButtonFemale;

    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        String id = SaveSharedPreference.getID(getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Customer/"+id);

        btnPurchaseHistory = (ImageButton)v.findViewById(R.id.btn_purchase_history);
        btnEditProfile = (ImageButton)v.findViewById(R.id.btn_edit_profile);
        btnForgetPassword = (ImageButton)v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton)v.findViewById(R.id.btn_logout);
        editTextName = (EditText)v.findViewById(R.id.editTextName);
        editTextEmail = (EditText)v.findViewById(R.id.editTextEmail);
        editTextAddress = (EditText)v.findViewById(R.id.editTextAddress);
        radioButtonMale = (RadioButton)v.findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton)v.findViewById(R.id.radioButtonFemale);


        btnSave = (Button)v.findViewById(R.id.btn_save);
        btnCancel = (Button)v.findViewById(R.id.btn_cancel);

        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Customer customer = documentSnapshot.toObject(Customer.class);
                editTextName.setText(customer.getName());
                editTextEmail.setText(customer.getEmail());
                editTextAddress.setText(customer.getAddress());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String male = radioButtonMale.getText().toString();
                String female = radioButtonFemale.getText().toString();
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String address = editTextAddress.getText().toString();
                mDocumentReference.update("name",name);
                mDocumentReference.update("email",email);
                mDocumentReference.update("address",address);
                if(radioButtonMale.isChecked())
                {
                    mDocumentReference.update("gender",male);
                }else{
                    mDocumentReference.update("gender",female);
                }
                Toast.makeText(getActivity(), "Profile details saved successfully", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                Fragment accountFragment = new AccountFragment();
                fm.beginTransaction().replace(R.id.fragment_container,accountFragment).commit();
            }
        });

        final FragmentManager fm = getFragmentManager();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment accountFragment = new AccountFragment();
                fm.beginTransaction().replace(R.id.fragment_container,accountFragment).commit();
            }
        });

        btnPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PurchaseHistoryActivity.class);
                startActivity(intent);
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

}
