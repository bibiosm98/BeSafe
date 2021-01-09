package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseImageRequest;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.UserBottomMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CourseSection extends AppCompatActivity {
    final String TAG = "Course Sections";

    JSONObject courseJSON;
    LinearLayout courseSections;
    String courseId;
    String courseSection;
    int courseSubsection;
    int subsectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_course_section);
        courseSections = findViewById(R.id.courseSectionLinearView);

        new UserBottomMenu().setOnClickMenu(this);
        loadCourseSection();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        Bundle b = new Bundle();
        b.putString("course", courseJSON.toString());
        intent = new Intent(CourseSection.this, CourseView.class);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public void loadCourseSection() {
        Bundle b = getIntent().getExtras();
        String value = "";
        if (b != null){
            value = b.getString("course");
            courseSection = b.getString("section");
            courseSubsection = Integer.parseInt(Objects.requireNonNull(b.getString("subsection")));
            subsectionCount = b.getInt("subsectionCount");
        }
        try {
            courseJSON = new JSONObject(value);

            ((TextView) findViewById(R.id.courseNameInSectionView)).setText(courseJSON.getString("name"));
            courseId = ((JSONObject) courseJSON.getJSONObject("course_id")).getString("$oid");
            Log.i("Name", courseJSON.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sectionRequest("user/course/" + courseId + "/" + courseSection + "/" + courseSubsection);
    }

    public void sectionRequest(String url){
        CourseRequest.getUserCourses(this, url, new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.i(TAG, result.toString(3));
                    loadSectionToView(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadSectionToView(JSONObject result){
        if(result!=null){
            try {

                ((TextView) findViewById(R.id.courseSubsectionName)).setText(((JSONObject) result.getJSONObject("response")).getString("name"));
                JSONArray subsections = result.getJSONObject("response").getJSONArray("contents");
                for(int i=0; i<subsections.length(); i++){
                    String type = ((JSONObject) subsections.get(i)).getString("type");
                    ConstraintLayout subsectionView = new ConstraintLayout(this);
                    TextView textView = new TextView(this);

                    subsectionView.setId(View.generateViewId());
                    textView.setId(View.generateViewId());

                    switch (type){
                        case "text":{
                            Log.i("Text", ((JSONObject) subsections.get(i)).getString("data"));
                            String data = ((JSONObject) subsections.get(i)).getString("data");

                            textView.setText(data);
                            textView.setTextSize(15);
                            textView.setTextColor(getColor(R.color.courseFontBlack));
                            textView.setPadding(40,60,40,0);
                            subsectionView.addView(textView);
                            courseSections.addView(subsectionView);
                            break;
                        }
                        case "picture":{
                            Log.i("picture", subsections.get(i).toString());
                            String data = ((JSONObject) subsections.get(i)).getString("data");
                            String url = getResources().getString(R.string.apiLink)+"/courses/"+"bhp1"+"/" + data;
                            String caption = ((JSONObject) subsections.get(i)).getString("caption");
                            textView.setText(caption);
                            subsectionView.addView(textView, 0);
                            courseSections.addView(subsectionView);

                            CourseImageRequest.getImage(this, url, i, new CourseImageRequest.VolleyCallback() {
                                @Override
                                public void onSuccess(Bitmap bitmap, int subsectionNumber) {
                                    ConstraintLayout imageView = (ConstraintLayout) courseSections.getChildAt(subsectionNumber);
                                    addImage(imageView, bitmap);
                                }
                            });

                            break;
                        }
                        case "video":{
                            Log.i("video", subsections.get(i).toString());
                            break;
                        }
                    }
                }


                ConstraintLayout nextButtonConstraint = new ConstraintLayout(this);
                ConstraintLayout nextButton = new ConstraintLayout(this);
                TextView buttonText = new TextView(this);

                int buttonConstraintId = View.generateViewId(),
                        buttonId = View.generateViewId(),
                        buttonTextId = View.generateViewId();
                nextButtonConstraint.setId(buttonConstraintId);
                nextButton.setId(buttonId);
                buttonText.setId(buttonTextId);

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 120);
                params.setMargins(40,50,40, 0);
//            my_courses.setOnClickListener(new func);
                nextButtonConstraint.setLayoutParams(params);

                ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(500, ConstraintLayout.LayoutParams.MATCH_PARENT);
//                params2.setMargins(40,50,40, 0);
//            my_courses.setOnClickListener(new func);
                nextButton.setLayoutParams(params2);

                Log.i("courseSubsection", " = " + String.valueOf(courseSubsection));
                Log.i("subsectionCount", " = " + String.valueOf(subsectionCount));
                if(courseSubsection == subsectionCount){
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CourseSection.this, "button OnClick FINISH", Toast.LENGTH_SHORT).show();
                            courseSections.removeAllViews();
                            Log.i("BACK", "BACK TO COURSE VIEW");
//                            finishAffinity();

                            Intent intent = new Intent(CourseSection.this, CourseView.class);
                            Bundle b = new Bundle();
                            b.putString("course", courseJSON.toString());

                            intent.putExtras(b); //Put your id to your next Intent
                            startActivity(intent);
                        }
                    });
                    buttonText.setText("Powrót ->");
                }else{
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CourseSection.this, "button OnClick", Toast.LENGTH_SHORT).show();
                            courseSections.removeAllViews();
                            Log.i("REQUEST", "user/course/" + courseId + "/" + courseSection + "/" + String.valueOf(courseSubsection +1));
                            sectionRequest("user/course/" + courseId + "/" + courseSection + "/" + String.valueOf(courseSubsection +1));
//                            courseSection = String.valueOf(Integer.parseInt(courseSection)+1);
                            courseSubsection++;
                        }
                    });
                    buttonText.setText("Przejdź dalej ->");
                }


                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.round_button_blue));
//                nextButtonConstraint.setBackground(ContextCompat.getDrawable(this, R.drawable.round_background_grey));
                buttonText.setTextColor(getResources().getColor(R.color.courseFontWhite));
                buttonText.setTextSize(20);

                nextButton.addView(buttonText);
                nextButtonConstraint.addView(nextButton);
                courseSections.addView(nextButtonConstraint);


                ConstraintSet set = new ConstraintSet();
                set.clone(nextButtonConstraint);
//                set.connect(buttonId, ConstraintSet.LEFT, buttonConstraintId, ConstraintSet.LEFT);
                set.connect(buttonId, ConstraintSet.RIGHT, buttonConstraintId, ConstraintSet.RIGHT);
                set.applyTo(nextButtonConstraint);

                ConstraintSet set2 = new ConstraintSet();
                set2.clone(nextButton);
                set2.connect(buttonTextId, ConstraintSet.LEFT, buttonId, ConstraintSet.LEFT);
                set2.connect(buttonTextId, ConstraintSet.RIGHT, buttonId, ConstraintSet.RIGHT);
                set2.connect(buttonTextId, ConstraintSet.TOP, buttonId, ConstraintSet.TOP);
                set2.connect(buttonTextId, ConstraintSet.BOTTOM, buttonId, ConstraintSet.BOTTOM);
                set2.applyTo(nextButton);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addImage(ConstraintLayout imageViewConstraint, Bitmap bitmap){
        ConstraintLayout imageView = new ConstraintLayout(this);
        imageView.setId(View.generateViewId());
        imageViewConstraint.setId(View.generateViewId());

        ImageView image = new ImageView(CourseSection.this);
        imageView.setBackgroundColor(getResources().getColor(R.color.white));
//                                  bitmap.setHeight(bitmap.getScaledHeight(200));
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 40, 30, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        BitmapDrawable ob = new BitmapDrawable(getResources(), output);
        image.setBackground(ob);
        image.setId(View.generateViewId());
        imageView.addView(image, 0);
//        imageViewConstraint.setBackground(ContextCompat.getDrawable(this, R.drawable.round_background_grey));
        imageViewConstraint.addView(imageView);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(bitmap.getWidth(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(40,50,40, 60);
        imageView.setLayoutParams(params);

        TextView pictureCaption = (TextView) imageViewConstraint.getChildAt(0);

        ConstraintSet set = new ConstraintSet();
        set.clone(imageViewConstraint);
        set.connect(imageView.getId(), ConstraintSet.LEFT, imageViewConstraint.getId(), ConstraintSet.LEFT);
        set.connect(imageView.getId(), ConstraintSet.RIGHT, imageViewConstraint.getId(), ConstraintSet.RIGHT);
        set.connect(imageView.getId(), ConstraintSet.TOP, imageViewConstraint.getId(), ConstraintSet.TOP);
        set.connect(imageView.getId(), ConstraintSet.BOTTOM, imageViewConstraint.getId(), ConstraintSet.BOTTOM);


        set.connect(pictureCaption.getId(), ConstraintSet.RIGHT, imageViewConstraint.getId(), ConstraintSet.RIGHT);
        set.connect(pictureCaption.getId(), ConstraintSet.LEFT, imageViewConstraint.getId(), ConstraintSet.LEFT);
        set.connect(pictureCaption.getId(), ConstraintSet.BOTTOM, imageViewConstraint.getId(), ConstraintSet.BOTTOM);

        set.applyTo(imageViewConstraint);

    }
}