package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.besafe.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // hidden keyboard

        setButton();
    }

    public void setButton(){
        Button button = findViewById(R.id.forgotPasswordBtn);
//        Toast.makeText(ForgotPassword.this, "Wysłano wiadomosc na adres Email", Toast.LENGTH_SHORT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ForgotPassword.this, "Wysłano wiadomość na adres Email \n Sprawdź swoją skrzynkę pocztową", Toast.LENGTH_LONG).show();
            }
        });
    }
}