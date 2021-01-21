package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.Requests.PasswordPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edittext = findViewById(R.id.userEmail);
                String email = edittext.getText().toString();
                JSONObject json = new JSONObject();
                try {
                    json.put("email", email);
                    PasswordPost.resetPassword(ForgotPassword.this, "forgot_password", json, new CourseRequest.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Toast.makeText(ForgotPassword.this, "Wysłano wiadomość na adres Email \n Sprawdź swoją skrzynkę pocztową", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}