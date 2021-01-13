package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.UserBottomMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UserProfil extends AppCompatActivity {
    String TAG = "UserProfil";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_user_profil);

        new UserBottomMenu().setOnClickMenu(this, true);
        getUserData();
    }


    public void getUserData(){
//        String url = getResources().getString(R.string.apiLink) + "api/user";
        String url = "/user";
        CourseRequest.getUserCourses(this, url, new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.i(TAG, result.toString(3));
                    loadUserInfoToView(result.getJSONObject("response"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadUserInfoToView(JSONObject response){
        String info = "";
        TextView textView;
        try {
            info = response.getString("first_name");
            info += " ";
            info += response.getString("surname");
            textView = findViewById(R.id.userName);
            textView.setText(info);

            info = response.getString("date_of_birth");
            textView = findViewById(R.id.userDate);
            textView.setText(info);

            info = response.getString("email");
            textView = findViewById(R.id.userEmail);
            textView.setText(info);

            JSONObject company = response.getJSONObject("company");

            info = company.getString("name");
            textView = findViewById(R.id.companyName);
            textView.setText(info);

            info = company.getString("contact_phone_number");
            textView = findViewById(R.id.companyNumber);
            textView.setText(info);

            info = company.getString("contact_email");
            textView = findViewById(R.id.companyEmail);
            textView.setText(info);

            info = company.getString("website");
            textView = findViewById(R.id.companyWebPageLink);
            final String finalInfo = info;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("WEB", "open Web Page: " + finalInfo);
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalInfo));
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application"));
                    startActivity(browserIntent);
                }
            });


            JSONObject supervisor = response.getJSONObject("supervisor");

            info = supervisor.getString("first_name");
            info += " ";
            info += supervisor.getString("surname");
            textView = findViewById(R.id.menagerName);
            textView.setText(info);

            info = supervisor.getString("email");
            textView = findViewById(R.id.menagerEmail);
            textView.setText(info);

            LinearLayout logoutLinear = (LinearLayout) findViewById(R.id.logOutLayout);
            logoutLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Button", "LOGOUT");
                    logOut();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logOut(){
        CourseRequest.getUserCourses(this, "/logout", new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.i("RESULT", result.toString(3));
                    if(result.getString("message").equals("OK")){
                        Intent intent = new Intent(UserProfil.this, Login.class);
                        Toast.makeText(UserProfil.this, "Wylogowano", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}