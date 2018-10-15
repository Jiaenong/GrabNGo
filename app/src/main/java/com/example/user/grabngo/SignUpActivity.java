package com.example.user.grabngo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity {
    private ImageView btn_back;
    private Button btnSignup;
    private EditText editTextEmailAddress, editTextPassword, editTextRetypePassword, editTextCustomerName, editTextAddress;
    private RadioButton radioButtonMale, radioButtonFemale;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;
    private CollectionReference mCollectionReference;

    private String autoID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmailAddress = (EditText)findViewById(R.id.editTextEmailAddress);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextRetypePassword = (EditText)findViewById(R.id.editTextRetypePassword);
        editTextCustomerName = (EditText)findViewById(R.id.editTextCustomerName);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        radioButtonMale = (RadioButton)findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton)findViewById(R.id.radioButtonFemale);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirebaseFirestore.collection("Customer");
        mCollectionReference.orderBy("email",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                     autoID = "C0001";
                }else{
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        String key = documentSnapshot.getId();
                        int num = Integer.parseInt(key.substring(3));
                        num++;
                        autoID = "C000"+num;
                        break;
                    }
                }
            }
        });

        btn_back = (ImageView)findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSignup = (Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editTextPassword.getText().toString();
                String retypepassword = editTextRetypePassword.getText().toString();
                if(!(retypepassword.equals(password)))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, R.style.AlertDialogCustom));
                    builder.setTitle("Sign Up Fail");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editTextEmailAddress.setText("");
                            editTextPassword.setText("");
                            editTextRetypePassword.setText("");
                            editTextAddress.setText("");
                            editTextCustomerName.setText("");
                            return;
                        }
                    });
                    builder.setMessage("Invalid retype password.");
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    SaveCustomer();
                }
            }
        });
    }

    private void SaveCustomer()
    {
        mDocumentReference = mFirebaseFirestore.document("Customer/"+autoID);
        String email = editTextEmailAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextCustomerName.getText().toString();
        String address = editTextAddress.getText().toString();
        String gender;
        if(radioButtonMale.isChecked()){
            gender = radioButtonMale.getText().toString();
        }else
            gender = radioButtonFemale.getText().toString();
        Customer customer = new Customer(email, password, name, address, gender);
        mDocumentReference.set(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, R.style.AlertDialogCustom));
                builder.setTitle("Sign Up Success");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                builder.setMessage("Please login your account to proceed.");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
