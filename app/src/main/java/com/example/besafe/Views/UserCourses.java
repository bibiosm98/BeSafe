package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseImageRequest;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.UserBottomMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class UserCourses extends AppCompatActivity {
    final static String TAG = "MyCoursesActivity";
    final String API = "https://bhpapi.herokuapp.com"; ///api/user/courses";

    JSONObject allUserCourses = null;
    LinearLayout layoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_courses);

        Objects.requireNonNull(getSupportActionBar()).hide();

        layoutList = findViewById(R.id.coursesLinear);

        new UserBottomMenu().setOnClickMenu(this, false);

        setTopMenuOnClick();
        getCourses(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setTopMenuOnClick(){

        TextView courses1 = (TextView) findViewById(R.id.coursesStartedBtn);
        TextView courses2 = (TextView) findViewById(R.id.coursesEndedBtn);

        courses1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked Kursy w trakcie trwania");
                v.setBackgroundResource(R.drawable.round_edittext_white);
                TextView courseBtn = (TextView) findViewById(R.id.coursesEndedBtn);
                courseBtn.setBackgroundResource(R.drawable.round_edittext_grey);
                layoutList.removeAllViews();
                getCourses(false);
            }
        });
        courses2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked Kursy zakończone");
                v.setBackgroundResource(R.drawable.round_edittext_white);
                TextView courseBtn = (TextView) findViewById(R.id.coursesStartedBtn);
                courseBtn.setBackgroundResource(R.drawable.round_edittext_grey);
                layoutList.removeAllViews();
                getCourses(true);
            }
        });
    }

    public void getCourses(final boolean endedCourses) {
        Log.i(TAG, "Function getCourses()");

        CourseRequest.getUserCourses(this, "user/courses", new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                allUserCourses = (JSONObject) result;
                if (result != null) {
                    try {
                        Log.i(TAG, allUserCourses.toString(3));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addCoursesToView(endedCourses);
                } else {
                    Log.i(TAG, "Null, brak pobranych kursów użytkownika");
                }
            }
        });
    }

    public void addCoursesToView(Boolean endedCourses) {
        Log.i(TAG, "addCoursesToView TextViewsy");
        JSONArray coursesList = null;
        try {
            coursesList = allUserCourses.getJSONArray("response");

            int start = 0;
            int counter = -1;
            Log.i(TAG, "Pętla, dodaje TextViewsy");
            for (int i = start; i < coursesList.length(); i++) {
                counter++;

                Log.i(TAG, "Wartość I = " + String.valueOf(counter));
                JSONObject course = (JSONObject) allUserCourses.getJSONArray("response").get(i);
                Log.i(TAG, "% = " + String.valueOf(course.get("completed_percent").toString()));
                if (endedCourses && ((Double) course.get("completed_percent")).intValue() != 100) {
                    counter--;
                    continue;
                }

                addCourseTextAndProgressBar(course, i - start);

                JSONObject JsonId = coursesList.getJSONObject(i).getJSONObject("course_id");
                String courseId = JsonId.getString("$oid");
                String file = course.getString("thumbnail");
//                String url = API + "/api/courses/" + "bhp1" + "/" + file;
                String url = API + "/api/courses/" + courseId + "/" + file;
                ;
                Log.i(TAG, "ulr do zdjęcia = " + url.toString());
                {
                    CourseImageRequest.getImage(this, url, counter, new CourseImageRequest.VolleyCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap, int courseNumber) {
                            ConstraintLayout course = (ConstraintLayout) layoutList.getChildAt(courseNumber);
                            //Image Backgroud operations
                            addImage(course, bitmap);
                        }
                    });
//                    addImage(course, Bitmap.createBitmap(null));
                }
            }
        } catch (JSONException e) {
            Log.i(TAG, "addCoursesToView brak odpowiedzi serwera");
            e.printStackTrace();
        }
    }

    //    public void addCourseToList(JSONArray coursesList, int i){
    public void addCourseTextAndProgressBar(final JSONObject courseInfo, int i) {
        try {
            int nameID = View.generateViewId(),
                    companyID = View.generateViewId(),
                    progressBarID = View.generateViewId(),
                    progressBarInfoID = View.generateViewId();
            String name = (String) courseInfo.get("name").toString();
            String company = (String) courseInfo.get("company").toString();
            String completed = (String) courseInfo.get("completed_percent").toString();
            Log.i(TAG, "Kurs nr:" + String.valueOf(i) + "name = " + name + "company = " + company);

            //ALL 3x TextView 1x ProgressBar
            {
                TextView nameTextView = new TextView(UserCourses.this);
                TextView companyTextView = new TextView(UserCourses.this);
                TextView progressBarInfo = new TextView(UserCourses.this);

                LayoutInflater inflater = getLayoutInflater();
                ConstraintLayout progressBarDadXD = (ConstraintLayout) inflater.inflate(R.layout.course_progress_bar, null);
                ProgressBar progressBar = (ProgressBar) progressBarDadXD.getChildAt(0);
                progressBarDadXD.removeAllViews();

                nameTextView.setId(nameID);
                nameTextView.setTextSize(23);
                nameTextView.setText(name);
                nameTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                nameTextView.setTextColor(getResources().getColor(R.color.courseFontWhite));
                nameTextView.setPadding(40, 0, 0, 0);

                companyTextView.setId(companyID);
                companyTextView.setTextSize(15);
                if (!courseInfo.get("company").toString().equals("null"))
                    companyTextView.setText(company);
//            companyTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                companyTextView.setTextColor(getResources().getColor(R.color.courseFontWhite));
                companyTextView.setPadding(40, 0, 0, 0);

                progressBar.setId(progressBarID);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress((int) Double.parseDouble(completed), true);
                } else {
                    progressBar.setProgress((int) Double.parseDouble(completed));
                }
//            progressBar.setPadding(0,0, 0,0);

                progressBarInfo.setId(progressBarInfoID);
                progressBarInfo.setTextSize(15);
                progressBarInfo.setText(String.format("%s%%", String.valueOf((int) Double.parseDouble(completed))));
                int complete = (int) Double.parseDouble(completed);
//            progressBarInfo.setText(String.valueOf(complete) + "%");
                progressBarInfo.setTextColor(getResources().getColor(R.color.courseFontWhite));
                progressBarInfo.setPadding(0, 0, 0, 0);

                final ConstraintLayout my_courses = (ConstraintLayout) getLayoutInflater().inflate(R.layout.course_on_list, null, false);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 250);
                params.setMargins(40, 50, 40, 0);
//            my_courses.setOnClickListener(new func);
                my_courses.setLayoutParams(params);

                my_courses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCourse(courseInfo);
                    }
                });
                my_courses.addView(nameTextView);
                my_courses.addView(companyTextView);
                my_courses.addView(progressBar);
                my_courses.addView(progressBarInfo);
                my_courses.setBackgroundResource(R.color.greyImage);
                layoutList.addView(my_courses);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addImage(ConstraintLayout courseView, Bitmap bitmap) {
        {
            ImageView image = new ImageView(UserCourses.this);
            courseView.setBackgroundColor(getResources().getColor(R.color.courseFontWhite));
//                            bitmap.setHeight(bitmap.getScaledHeight(200));
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
            courseView.addView(image, 0);
        }

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 30, getResources().getDisplayMetrics());
        ConstraintSet set = new ConstraintSet();
        set.clone(courseView);
        set.connect(courseView.getChildAt(2).getId(), ConstraintSet.BOTTOM, courseView.getChildAt(0).getId(), ConstraintSet.BOTTOM);
        set.connect(courseView.getChildAt(1).getId(), ConstraintSet.BOTTOM, courseView.getChildAt(2).getId(), ConstraintSet.TOP);
        set.connect(courseView.getChildAt(3).getId(), ConstraintSet.BOTTOM, courseView.getChildAt(0).getId(), ConstraintSet.BOTTOM, margin);
        set.connect(courseView.getChildAt(3).getId(), ConstraintSet.RIGHT, courseView.getChildAt(0).getId(), ConstraintSet.RIGHT, margin);
//        set.connect(courseView.getChildAt(3).getId(), ConstraintSet.BOTTOM, courseView.getId(), ConstraintSet.BOTTOM, margin);
//        set.connect(courseView.getChildAt(3).getId(), ConstraintSet.RIGHT, courseView.getId(), ConstraintSet.RIGHT, margin);
//        set.connect(courseView.getChildAt(0).getId(),ConstraintSet.LEFT, courseView.getId(), ConstraintSet.LEFT, margin);
        set.connect(courseView.getChildAt(0).getId(),ConstraintSet.RIGHT, courseView.getId(), ConstraintSet.RIGHT, margin);


        set.centerHorizontally(courseView.getChildAt(4).getId(), courseView.getChildAt(3).getId());
        set.centerVertically(courseView.getChildAt(4).getId(), courseView.getChildAt(3).getId());
        set.applyTo(courseView);
    }

    public void sortViewsByName() {
        LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.coursesLinear);
        int childcount = myLinearLayout.getChildCount();
        List<View> children = new ArrayList<>();

        for (int i = 0; i < childcount; i++) {
            children.add(myLinearLayout.getChildAt(i));
        }

        Collections.sort(children, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                TextView view1 = (TextView) ((ViewGroup) o1).getChildAt(1);
                TextView view2 = (TextView) ((ViewGroup) o2).getChildAt(1);
                return view1.getText().toString().compareTo(view2.getText().toString());
            }
        });

        myLinearLayout.removeAllViews();
        for (int i = 0; i < childcount; i++) {
            myLinearLayout.addView(children.get(i));
        }
    }

    public void openCourse(JSONObject courseInfo) {
        Intent intent = new Intent(UserCourses.this, CourseView.class);
        Bundle b = new Bundle();
        b.putString("course", courseInfo.toString());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}