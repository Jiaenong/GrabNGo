package com.example.user.grabngo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText editTextRecoverEmail;
    private Button btnRecoverOkay;

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private DocumentReference mDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        setTitle("Forget Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editTextRecoverEmail = (EditText)findViewById(R.id.editTextRecoverEmail);
        btnRecoverOkay = (Button)findViewById(R.id.btnRecoverOkay);

        mFirebaseFirestore = FirebaseFirestore.getInstance();

        btnRecoverOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextRecoverEmail.getText().toString();
                sendMail(email);
            }
        });
    }

    private void sendMail(String email) {
        Random random = new Random();
        String randomNum = String.format("%04d",random.nextInt(10000));
        String subject = "Forget Password";
        String message = "Below is the temporary password for you to login into your account, " +
                "please change the password immediately after login to the system succesfully"+"\n"+"\n"+"4 digit :"+randomNum;

        mCollectionReference = mFirebaseFirestore.collection("Customer");
        mCollectionReference.whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String id = "";
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    id = documentSnapshot.getId();
                }
                mDocumentReference = mFirebaseFirestore.document("Customer/"+id);
                mDocumentReference.update("password",randomNum);
            }
        });
        SendeMail sm = new SendeMail(ForgetPasswordActivity.this, email, subject, message);
        sm.execute();
        Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
        intent.putExtra("tag","TAG");
        startActivity(intent);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
