package com.example.user.grabngo;

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

public class LoginActivity extends AppCompatActivity {

    private Button btnSignUp, btnLogin;
    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btnSignUp = (Button)findViewById(R.id.btn_signup);
        btnLogin = (Button)findViewById(R.id.btn_login);
        editTextEmail = (EditText)findViewById(R.id.editText_email);
        editTextPassword = (EditText)findViewById(R.id.editText_password);

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


                if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")){
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

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
