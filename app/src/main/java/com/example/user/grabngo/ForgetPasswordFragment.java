package com.example.user.grabngo;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {

    private Button btnSave, btnCancel;
    private ImageButton btnPurchaseHistory, btnEditProfile, btnForgetPassword, btnLogout;
    private EditText editTextOldPassword, editTextNewPassword, editTextRetypePassword;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;

    public HomeActivity homeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forget_password, container, false);
        String id = SaveSharedPreference.getID(getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDocumentReference = mFirebaseFirestore.document("Customer/"+id);

        btnPurchaseHistory = (ImageButton)v.findViewById(R.id.btn_purchase_history);
        btnEditProfile = (ImageButton)v.findViewById(R.id.btn_edit_profile);
        btnForgetPassword = (ImageButton)v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton)v.findViewById(R.id.btn_logout);
        editTextOldPassword = (EditText)v.findViewById(R.id.editTextOldPassword);
        editTextNewPassword = (EditText)v.findViewById(R.id.editTextNewPassword);
        editTextRetypePassword = (EditText)v.findViewById(R.id.editTextRetypePassword);
        btnSave = (Button)v.findViewById(R.id.btn_save);
        btnCancel = (Button)v.findViewById(R.id.btn_cancel);
        homeActivity = (HomeActivity)getActivity();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String oldPass = editTextOldPassword.getText().toString();
                        String newPass = editTextNewPassword.getText().toString();
                        String retypePass = editTextRetypePassword.getText().toString();
                        Customer customer = documentSnapshot.toObject(Customer.class);
                        String password = customer.getPassword();
                        Log.i("Testing",password);
                        if(oldPass.equals("") || newPass.equals("") || retypePass.equals(""))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("All field must be fill up !");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }else if(!(retypePass.equals(newPass)))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Password not match with new password !");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }else if(!(oldPass.equals(password)))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Password not match with old password !");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else{
                            mDocumentReference.update("password",newPass);
                            Toast.makeText(getActivity(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            FragmentManager fm = getFragmentManager();
                            Fragment accountFragment = new AccountFragment();
                            fm.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
                        }
                    }
                });

            }
        });

        final FragmentManager fm = getFragmentManager();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.tag = null;
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

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment editProfileFragment = new EditProfileFragment();
                fm.beginTransaction().replace(R.id.fragment_container,editProfileFragment).commit();
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
