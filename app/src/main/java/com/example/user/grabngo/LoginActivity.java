package com.example.user.grabngo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.grabngo.Admin.ManagerHomeActivity;
import com.example.user.grabngo.Admin.StaffHomeActivity;
import com.example.user.grabngo.Class.Customer;
import com.example.user.grabngo.Class.Staff;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnLogin;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBarLogIn;
    private View view1, view2;
    private TextView textViewForgetPassword;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private CollectionReference mStaffCollectionReference;
    private CollectionReference mManagerCollectionReference;

    private int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btnSignUp = (Button)findViewById(R.id.btn_signup);
        btnLogin = (Button)findViewById(R.id.btn_login);
        editTextEmail = (EditText)findViewById(R.id.editText_email);
        editTextPassword = (EditText)findViewById(R.id.editText_password);
        progressBarLogIn = (ProgressBar)findViewById(R.id.progressBarLogIn);
        textViewForgetPassword = (TextView)findViewById(R.id.forgetPassword);
        view1 = (View)findViewById(R.id.view1);
        view2 = (View)findViewById(R.id.view2);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Customer");
        mStaffCollectionReference = mFirebaseFirestore.collection("Staff");
        mManagerCollectionReference = mFirebaseFirestore.collection("Manager");

        progressBarLogIn.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEmail.setText("");
                editTextPassword.setText("");
                editTextPassword.clearFocus();
                editTextEmail.clearFocus();
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Hide keyboard when onclick
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                progressBarLogIn.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.GONE);
                editTextPassword.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.GONE);
                textViewForgetPassword.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);

                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();


                if(email.equals("")||password.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AlertDialogCustom));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBarLogIn.setVisibility(View.GONE);
                            editTextEmail.setVisibility(View.VISIBLE);
                            editTextPassword.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            btnSignUp.setVisibility(View.VISIBLE);
                            textViewForgetPassword.setVisibility(View.VISIBLE);
                            view1.setVisibility(View.VISIBLE);
                            view2.setVisibility(View.VISIBLE);
                            return;
                        }
                    });
                    builder.setTitle("Error");
                    builder.setMessage("Email address and password are required to proceed");
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;

                }else {

                    mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Customer customer = documentSnapshot.toObject(Customer.class);
                                String email = editTextEmail.getText().toString();
                                String password = editTextPassword.getText().toString();
                                if (email.equals(customer.getEmail()) && password.equals(customer.getPassword())) {
                                    check++;
                                    String id = documentSnapshot.getId();
                                    SaveSharedPreference.setID(LoginActivity.this,id);
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                            }
                            if (check == 0) {
                                mStaffCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Staff staff = documentSnapshot.toObject(Staff.class);
                                            if (email.equals(staff.getEmail()) && password.equals(staff.getPassword())) {
                                                check++;
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("username",staff.getName());
                                                editor.apply();
                                                Intent intent = new Intent(LoginActivity.this, StaffHomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                                break;
                                            }
                                        }

                                        if (check == 0) {
                                            mManagerCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        Staff manager = documentSnapshot.toObject(Staff.class);
                                                        if (email.equals(manager.getEmail()) && password.equals(manager.getPassword())) {
                                                            check++;
                                                            Intent intent = new Intent(LoginActivity.this, ManagerHomeActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                            break;
                                                        }
                                                    }

                                                    if(check==0){

                                                        editTextEmail.setText("");
                                                        editTextPassword.setText("");

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AlertDialogCustom));
                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                progressBarLogIn.setVisibility(View.GONE);
                                                                editTextEmail.setVisibility(View.VISIBLE);
                                                                editTextPassword.setVisibility(View.VISIBLE);
                                                                btnLogin.setVisibility(View.VISIBLE);
                                                                btnSignUp.setVisibility(View.VISIBLE);
                                                                textViewForgetPassword.setVisibility(View.VISIBLE);
                                                                view1.setVisibility(View.VISIBLE);
                                                                view2.setVisibility(View.VISIBLE);
                                                                return;
                                                            }
                                                        });
                                                        builder.setTitle("Error");
                                                        builder.setMessage("Incorrect email and password !");
                                                        AlertDialog alert = builder.create();
                                                        alert.show();
                                                    }

                                                }


                                            });
                                        }

                                    }


                                });
                            }
                        }
                    });

                }

            }
        });
    }
}
