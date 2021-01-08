package com.example.besafe.Views;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.besafe.R;
import com.example.besafe.Requests.LoginRequest;

import org.json.JSONObject;

public class Login extends AppCompatActivity{
    final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // hidden keyboard
        setOnclick();
    }

    public void setOnclick(){
        Button loginButton = (Button) findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email, password;
                email = findViewById(R.id.editTextPersonName);
                password = findViewById(R.id.editTextTextPassword);
                LoginRequest.logIn(Login.this, email.getText().toString(), password.getText().toString());
            }
        });

        TextView  forgotPassword = (TextView) findViewById(R.id.forgotPasswordBtn);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }
}
