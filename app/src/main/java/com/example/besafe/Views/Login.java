package com.example.besafe.Views;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
    }


    public void logInButton(View view){
        Log.i(TAG, "Login Button Clicked");

        JSONObject object = new JSONObject();
        EditText email, password;

        email = findViewById(R.id.editTextPersonName);
        password = findViewById(R.id.editTextTextPassword);
        LoginRequest.logIn(this, email.getText().toString(), password.getText().toString());
    }
}
