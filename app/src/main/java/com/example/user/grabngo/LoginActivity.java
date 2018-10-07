package com.example.user.grabngo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.grabngo.Admin.ManagerHomeActivity;
import com.example.user.grabngo.Admin.StaffHomeActivity;
import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnLogin;
    private EditText editTextEmail, editTextPassword;
    private ProgressDialog pDialog;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btnSignUp = (Button)findViewById(R.id.btn_signup);
        btnLogin = (Button)findViewById(R.id.btn_login);
        editTextEmail = (EditText)findViewById(R.id.editText_email);
        editTextPassword = (EditText)findViewById(R.id.editText_password);
        pDialog = new ProgressDialog(this);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Customer");

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
                if(!pDialog.isShowing())
                    pDialog.setMessage("Signing in...");
                pDialog.show();
                mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            Customer customer = documentSnapshot.toObject(Customer.class);
                            String email = editTextEmail.getText().toString();
                            String password = editTextPassword.getText().toString();
                            if(email.equals(customer.getEmail()) && password.equals(customer.getPassword()))
                            {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                if(pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        }
                    }
                });

                if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AlertDialogCustom));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.setTitle("Error");
                    builder.setMessage("Email address and password are required to proceed");
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }else{
                    if(editTextEmail.getText().toString().equals("abc")&&editTextPassword.getText().toString().equals("abc")){
                        Intent intent = new Intent(LoginActivity.this, StaffHomeActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(editTextEmail.getText().toString().equals("xyz")&&editTextPassword.getText().toString().equals("xyz")){
                        Intent intent = new Intent(LoginActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
            }
        });
    }
}
