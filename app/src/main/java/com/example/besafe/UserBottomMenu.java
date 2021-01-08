package com.example.besafe;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.besafe.Views.UserProfil;

public class UserBottomMenu {
    Activity activity;
    public void setOnClickMenu(final Activity _activity){
        this.activity = _activity;
        _activity.findViewById(R.id.menuLayout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "NOT THIS TIME", Toast.LENGTH_SHORT).show();
            }
        });
        ConstraintLayout group = _activity.findViewById(R.id.userCoursesMenuBtn);
        group.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "USER COURSES BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
        int refIds = group.getChildCount();
        for (int id=0; id<refIds; id++) {
            group.getChildAt(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "USER COURSES BUTTON", Toast.LENGTH_SHORT).show();
                }
            });
        }
        group = _activity.findViewById(R.id.userProfilMenuBtn);
        group.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "USER PROFIL BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
        refIds = group.getChildCount();
        for (int id=0; id<refIds; id++) {
            group.getChildAt(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "USER PROFIL BUTTON", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(_activity.getBaseContext(), UserProfil.class);
                    _activity.startActivity(intent);
                }
            });
        }
    }
}
