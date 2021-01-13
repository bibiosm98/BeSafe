package com.example.besafe;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.besafe.Views.UserCourses;
import com.example.besafe.Views.UserProfil;


public class UserBottomMenu {
    Activity activity;
    public void setOnClickMenu(final Activity _activity, boolean profilView){
        this.activity = _activity;
        _activity.findViewById(R.id.menuLayout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "NOT THIS TIME", Toast.LENGTH_SHORT).show();
            }
        });
        ConstraintLayout group;
        int refIds;

        if(profilView){

            group = _activity.findViewById(R.id.userCoursesMenuBtn);
            group.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(_activity.getBaseContext(), UserCourses.class);
                    _activity.startActivity(intent);

                }
            });
            refIds = group.getChildCount();
            for (int id=0; id<refIds; id++) {
                group.getChildAt(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(_activity.getBaseContext(), UserCourses.class);
                        _activity.startActivity(intent);
                    }
                });
            }

            ImageView image = _activity.findViewById(R.id.coursesViewImage);
            image.setBackgroundResource(R.mipmap.courses_icon_grey_white_xxxhdpi);
            TextView textView = _activity.findViewById(R.id.userMenuCursesTextView);
            textView.setTextColor(_activity.getResources().getColor(R.color.courseFontGrey));

            image = _activity.findViewById(R.id.userViewImage);
            image.setBackgroundResource(R.mipmap.user_icon_blue_grey_xxxhdpi);
            textView = _activity.findViewById(R.id.userMenuProfilTextView);
            textView.setTextColor(_activity.getResources().getColor(R.color.courseFontBlue));

            LinearLayout linear = _activity.findViewById(R.id.userMenuLayout);
            linear.setBackground(_activity.getResources().getDrawable(R.drawable.background_white));

        }else{
            group = _activity.findViewById(R.id.userProfilMenuBtn);
            group.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(_activity.getBaseContext(), UserProfil.class);
                    _activity.startActivity(intent);
                }
            });
            refIds = group.getChildCount();
            for (int id=0; id<refIds; id++) {
                group.getChildAt(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(_activity.getBaseContext(), UserProfil.class);
                        _activity.startActivity(intent);
                    }
                });
            }

            ImageView image = _activity.findViewById(R.id.coursesViewImage);
            image.setBackgroundResource(R.mipmap.courses_icon_blue_grey_xxxhdpi);
            TextView textView = _activity.findViewById(R.id.userMenuCursesTextView);
            textView.setTextColor(_activity.getResources().getColor(R.color.courseFontBlue));

            image = _activity.findViewById(R.id.userViewImage);
            image.setBackgroundResource(R.mipmap.user_icon_grey_xxxhdpi);
            textView = _activity.findViewById(R.id.userMenuProfilTextView);
            textView.setTextColor(_activity.getResources().getColor(R.color.courseFontGrey));

            LinearLayout linear = _activity.findViewById(R.id.userMenuLayout);
            linear.setBackground(_activity.getResources().getDrawable(R.drawable.background_grey));
        }
    }
}
