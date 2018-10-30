package com.example.user.grabngo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.user.grabngo.Class.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SignUpActivity extends AppCompatActivity {
    private ImageView btn_back;
    private Button btnSignup;
    private EditText editTextEmailAddress, editTextPassword, editTextRetypePassword, editTextCustomerName, editTextAddress;
    private RadioButton radioButtonMale, radioButtonFemale;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference mDocumentReference;
    private CollectionReference mCollectionReference;

    private String autoID;
    private int check = 0;
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
        /*mCollectionReference.orderBy("email",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                     autoID = "C0001";
                }else{
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    {
                        String key = documentSnapshot.getId();
                        Log.i("key ", key);
                        int num = Integer.parseInt(key.substring(3));
                        num++;
                        autoID = "C000"+num;
                        break;
                    }
                }
            }
        });*/

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
                String email = editTextEmailAddress.getText().toString();
                String name = editTextCustomerName.getText().toString();
                String address = editTextAddress.getText().toString();
                String retypepassword = editTextRetypePassword.getText().toString();
                mCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            Customer customer = documentSnapshot.toObject(Customer.class);
                            String customerEmail = customer.getEmail();
                            if(email.equals(customerEmail))
                            {
                                check++;
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
                                        check = 0;
                                        return;
                                    }
                                });
                                builder.setMessage("This email has already been register, use another email.");
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                        if(!(retypepassword.equals(password)))
                        {
                            check++;
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
                                    check = 0;
                                    return;
                                }
                            });
                            builder.setMessage("Invalid retype password.");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }else if(password.equals("")||email.equals("")||name.equals("")||address.equals("")||retypepassword.equals(""))
                        {
                            check++;
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
                                    check = 0;
                                    return;
                                }
                            });
                            builder.setMessage("All field are required to enter !");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else if(isEmailValid(email) == false)
                        {
                            check++;
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
                                    check = 0;
                                    return;
                                }
                            });
                            builder.setMessage("Invalid email format !");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        if(check == 0)
                        {
                            SaveCustomer();
                        }
                    }
                });
            }
        });
    }

    public static boolean isEmailValid(String email) {
        Log.i("Testing", "hello");
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void SaveCustomer()
    {
        mDocumentReference = mFirebaseFirestore.document("Customer/"+autoID);
        String pic = "";
        final String email = editTextEmailAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextCustomerName.getText().toString();
        String address = editTextAddress.getText().toString();
        String gender;
        if(radioButtonMale.isChecked()){
            gender = radioButtonMale.getText().toString();
        }else
            gender = radioButtonFemale.getText().toString();
        Customer customer = new Customer(email, password, name, gender, address, pic);
        mCollectionReference.add(customer).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SignUpActivity.this, R.style.AlertDialogCustom));
                builder.setTitle("Sign Up Success");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        emailSend(email);
                        finish();
                        //Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivity(intent);
                    }
                });
                builder.setMessage("Please login your account to proceed.");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        /*mDocumentReference.set(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });*/
    }

    private void emailSend(String email) {
        String subject = "Registration Successfull !";
        String message = "Welcome to GrabNGo, from now on you can start enjoy using the service provided by our system.";

        SendeMail sm = new SendeMail(SignUpActivity.this, email, subject, message);
        sm.execute();

        return;
    }
}

