package com.example.besafe.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.Requests.LoginRequest;

import org.json.JSONObject;

import java.util.Objects;

public class Login extends AppCompatActivity{
//    final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);


        final LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.loginAnimation);
//        animationView.enableHardwareAcceleration(true);
        animationView.buildDrawingCache(true);
        animationView.enableMergePathsForKitKatAndAbove(true);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // hidden keyboard
        setOnclick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //EXIT APP AFTER LOGOUT
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void setOnclick(){
        Button loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email, password;
                email = findViewById(R.id.editTextPersonName);
                password = findViewById(R.id.editTextTextPassword);
                LoginRequest.logIn(Login.this, email.getText().toString(), password.getText().toString(), new CourseRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Intent intent = new Intent(Login.this, UserCourses.class);
                        startActivity(intent);
                    }
                });
            }
        });

        TextView  forgotPassword = findViewById(R.id.forgotPasswordBtn);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }
}
