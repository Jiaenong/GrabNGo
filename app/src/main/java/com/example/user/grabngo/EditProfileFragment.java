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
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private Button btnSave, btnCancel;
    private ImageButton btnPurchaseHistory, btnEditProfile, btnForgetPassword, btnLogout;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        btnPurchaseHistory = (ImageButton)v.findViewById(R.id.btn_purchase_history);
        btnEditProfile = (ImageButton)v.findViewById(R.id.btn_edit_profile);
        btnForgetPassword = (ImageButton)v.findViewById(R.id.btn_forget_password);
        btnLogout = (ImageButton)v.findViewById(R.id.btn_logout);


        btnSave = (Button)v.findViewById(R.id.btn_save);
        btnCancel = (Button)v.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
