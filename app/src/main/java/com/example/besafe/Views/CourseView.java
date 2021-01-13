package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.UserBottomMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CourseView extends AppCompatActivity {
    String TAG = "CourseView";
    LinearLayout sectionsList;
    JSONObject courseJSON;
    String courseID;
    int allSubsections = 0;
    int subsectionsProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_course_view);

        sectionsList = findViewById(R.id.sectionsListLinear);
        new UserBottomMenu().setOnClickMenu(this, false);
        getBundle();
        try {
            courseID = courseJSON.getJSONObject("course_id").getString("$oid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadCourse(courseJSON);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        intent = new Intent(CourseView.this, UserCourses.class);
        startActivity(intent);
        finish();
    }

    public void getBundle(){
        Bundle b = getIntent().getExtras();
        String value = "";
        if(b != null){
            value = b.getString("course");
            Log.i("Bundle  = ", value);
            try {
                courseJSON = new JSONObject(value);
                Log.i("Name ", courseJSON.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.i("CourseView", "Empty Bundle \"course\" ");
        }
    }

    public void loadCourse(JSONObject course){
        CourseRequest.getUserCourses(this, "user/course/" + courseID, new CourseRequest.VolleyCallback(){
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    courseJSON = result.getJSONObject("response");
                    Log.i(TAG, result.toString(3));
                    loadCourseInfo(result.getJSONObject("response"));
                    loadSections(result.getJSONObject("response").getJSONArray("sections"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadCourseInfo(JSONObject response) {

        TextView courseName = (TextView) findViewById(R.id.courseName);
        TextView courseFinishDate = (TextView) findViewById(R.id.courseFinishDate);
        TextView completedSections = (TextView) findViewById(R.id.completedSections);
        ProgressBar courseProgressBar = (ProgressBar) findViewById(R.id.courseProgressBar);

        TextView courseInfoValue = (TextView) findViewById(R.id.courseInfoValue);
        TextView version = (TextView) findViewById(R.id.versionValue);
        TextView categoryValue = (TextView) findViewById(R.id.categoryValue);
        TextView levelValue = (TextView) findViewById(R.id.levelValue);

        try {
            courseName.setText(response.getString("name"));
            String newDate = courseFinishDate.getText() + response.getString("deadline");
            courseFinishDate.setText(newDate);

            Double completed = response.getDouble("completed_percent");
            int completedInt = completed.intValue();
            courseProgressBar.setProgress(completedInt);
            int sectionsCount = response.getJSONArray("sections").length();

            Log.i(TAG, "ILE: " + completedInt);
//            completedInt = completedInt / 100 * sectionsCount;
            completedInt++;
            completedInt = (completedInt * sectionsCount) / 100 ;
            Log.i(TAG, "ILE2: " + completedInt);
            String sectionFinished = completedSections.getText() + String.valueOf(completedInt) + " z "+ String.valueOf(sectionsCount);

            completedSections.setText(sectionFinished);


            courseInfoValue.setText(response.getString("description"));

            JSONArray properties = response.getJSONArray("properties");
            version.setText( ((JSONObject) properties.get(0)).getString("value"));
            categoryValue.setText( ((JSONObject) properties.get(1)).getString("value"));
            levelValue.setText( ((JSONObject) properties.get(2)).getString("value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadSections(JSONArray sections){
        final int sectionCount = sections.length();
        int complete_percent = 0;
        try {
            complete_percent = (int) Double.parseDouble(courseJSON.getString("completed_percent"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout sectionLinear = findViewById(R.id.sectionsListLinear);

        String name, description;
        JSONObject section;
        JSONArray subsections;
        allSubsections = 0;
        for(int i=0; i< sections.length(); i++) {
            try {
                section = (JSONObject) sections.get(i);
                subsections = section.getJSONArray("subsections");
                allSubsections += subsections.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i=0; i< sections.length(); i++){


            try {
                ConstraintLayout sectionConstraint = (ConstraintLayout) getLayoutInflater().inflate(R.layout.course_on_list, null);

                section = (JSONObject) sections.get(i);
                name = section.getString("name");
                description = section.getString("description");
                subsections = section.getJSONArray("subsections");
                final int subsectionCount = subsections.length();

                TextView sectionName = new TextView(CourseView.this);
                TextView sectionDescription = new TextView(CourseView.this);
                View sectionProgress;
                ConstraintLayout progressBarDadXD = (ConstraintLayout) getLayoutInflater().inflate(R.layout.progress_bar, null);
                ProgressBar sectionProgressBar = (ProgressBar) progressBarDadXD.getChildAt(0);
                progressBarDadXD.removeAllViews();

                sectionConstraint.setId(View.generateViewId());
                sectionName.setId(View.generateViewId());
                sectionDescription.setId(View.generateViewId());
                sectionProgressBar.setId(View.generateViewId());

                sectionName.setText(name);
                sectionDescription.setText(description);

                int progressValue = 0;
                int oneSection = 100/sectionCount;

                Log.i(TAG, "complete_percent = "+ complete_percent);
                Log.i(TAG, "sectionCount = "+ sectionCount);

                if(oneSection*(i) < complete_percent){
                    progressValue = 0;
                }
                if(oneSection*(i+0.1)-1 < complete_percent){
//                    progressValue = (100/subsectionCount) * ;
                    subsectionsProgress = getfinishedSub(i);
                    Log.i("PROGRES", String.valueOf(getfinishedSub(i)));
                    Log.i("PROGRES 2","complete_percent - subsectionsProgress)*100  = " + (complete_percent - subsectionsProgress)*100 );
                    progressValue = (complete_percent - subsectionsProgress)*100 / (100*subsectionCount/allSubsections); // Don't look at this, will be fix soon
                }
                if(oneSection*(i+1) <= complete_percent){
                    progressValue = 100;
                }

                ConstraintLayout lastPercent;
                int progress;
                if(i>0) {
                    lastPercent = (ConstraintLayout) sectionsList.getChildAt(i - 1);
                    progress = ((ProgressBar) lastPercent.getChildAt(2)).getProgress();

                    if(progress < 100){
                        sectionProgress = new ImageView(CourseView.this);
                        sectionProgress.setId(View.generateViewId());

                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(50, 50);
                        sectionProgress.setLayoutParams(params);
                        sectionProgress.setBackground(getResources().getDrawable(R.mipmap.icon_lock_grey_xxxhdpi));
                    }else{
                        sectionProgress = new TextView(CourseView.this);
                        sectionProgress.setId(View.generateViewId());

                        ((TextView) sectionProgress).setTextColor(getResources().getColor(R.color.courseFontBlack));
                        ((TextView) sectionProgress).setTextSize(15);
                        ((TextView) sectionProgress).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        ((TextView) sectionProgress).setText(String.valueOf(progressValue) + "%");
                    }


                    if(progress == 100) {
                        //sectionProgress.setText(String.valueOf(progressValue) + "%");
                        final int finalI = i;
                        sectionConstraint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = null;
                                intent = new Intent(CourseView.this, CourseSection.class);
                                Bundle b = new Bundle();
                                b.putString("course", courseJSON.toString());
                                b.putString("sectionConstraint", String.valueOf(finalI + 1));
                                b.putString("section", String.valueOf(finalI +1));
                                b.putString("subsection", String.valueOf(1));
                                b.putInt("subsectionCount", subsectionCount);

                                intent.putExtras(b);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }else{
                    final int finalI = i;
                    sectionProgress = new TextView(CourseView.this);
                    sectionProgress.setId(View.generateViewId());

                    ((TextView) sectionProgress).setTextColor(getResources().getColor(R.color.courseFontBlack));
                    ((TextView) sectionProgress).setTextSize(15);
                    ((TextView) sectionProgress).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    ((TextView) sectionProgress).setText(String.valueOf(progressValue) + "%");

                    final int finalI1 = i;
                    sectionConstraint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = null;
                            intent = new Intent(CourseView.this, CourseSection.class);
                            Bundle b = new Bundle();
                            b.putString("course", courseJSON.toString());
                            b.putString("sectionConstraint", String.valueOf(finalI + 1));
                            b.putString("section", String.valueOf(finalI1+1));
                            b.putString("subsection", String.valueOf(1));
                            b.putInt("subsectionCount", subsectionCount);

                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                Log.i(TAG, "oneSection*(i+1) = "+ oneSection*(i+1));
                Log.i(TAG, "oneSection*(i) = "+ oneSection*(i));
                sectionProgress.setPadding(0,0,0,0);
                sectionProgressBar.setProgress(progressValue);


                sectionConstraint.setPadding(30,20,30,10);
//                sectionName.setPadding(30,10,10,10);
                sectionDescription.setPadding(40,0,160,20);
//
//                Log.i(TAG, "DESCRIPTION,LENGTH = " + description.length());
//                Log.i(TAG, "DESCRIPTION,LENGTH 2 = " + (description.length()%20)*50+250);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (description.length()/20)*50+50); //ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(40,50,40, 10);
//                sectionConstraint.setOnClickListener(new func);
                sectionConstraint.setLayoutParams(params);


//                sectionConstraint.setBackgroundColor(getResources().getColor(R.color.blueActionButton));
                sectionConstraint.setBackgroundResource(R.drawable.section_background);
                sectionName.setTextSize(20);
                sectionName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                sectionName.setTextColor(getResources().getColor(R.color.courseFontBlack));
                sectionDescription.setTextSize(15);
                sectionDescription.setTextColor(getResources().getColor(R.color.courseFontBlack));
//                sectionDescription.setWidth();

                sectionConstraint.addView(sectionName);
                sectionConstraint.addView(sectionDescription);
                sectionConstraint.addView(sectionProgressBar);
                sectionConstraint.addView(sectionProgress);


//                sectionConstraint.setMinimumHeight((description.length()%20)*50+50);
                ConstraintSet sectionSet = new ConstraintSet();
                sectionSet.clone(sectionConstraint);
//                Toast.makeText(this, "Height = "+ sectionConstraint.getHeight(), Toast.LENGTH_LONG);
                Log.i(TAG, "Height = "+ sectionConstraint.getHeight());


                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 40, getResources().getDisplayMetrics());

                sectionSet.connect(sectionName.getId(), ConstraintSet.TOP, sectionConstraint.getId(), ConstraintSet.TOP);
                sectionSet.connect(sectionName.getId(), ConstraintSet.LEFT, sectionConstraint.getId(), ConstraintSet.LEFT, margin);
                sectionSet.connect(sectionDescription.getId(), ConstraintSet.BOTTOM, sectionDescription.getId(), ConstraintSet.TOP);

//                sectionSet.connect(sectionProgressBar.getId(), ConstraintSet.RIGHT, sectionConstraint.getId(), ConstraintSet.RIGHT);
//                sectionSet.connect(sectionProgressBar.getId(), ConstraintSet.RIGHT, sectionDescription.getId(), ConstraintSet.RIGHT);

                sectionSet.connect(sectionDescription.getId(), ConstraintSet.TOP, sectionName.getId(), ConstraintSet.BOTTOM);
                sectionSet.connect(sectionDescription.getId(), ConstraintSet.LEFT, sectionConstraint.getId(), ConstraintSet.LEFT);
                sectionSet.connect(sectionDescription.getId(), ConstraintSet.BOTTOM, sectionConstraint.getId(), ConstraintSet.BOTTOM);
//                sectionSet.connect(sectionDescription.getId(), ConstraintSet.RIGHT, sectionProgressBar.getId(), ConstraintSet.LEFT);

                sectionSet.connect(sectionProgressBar.getId(), ConstraintSet.TOP, sectionConstraint.getId(), ConstraintSet.TOP);
                sectionSet.connect(sectionProgressBar.getId(), ConstraintSet.BOTTOM, sectionConstraint.getId(), ConstraintSet.BOTTOM);
                sectionSet.connect(sectionProgressBar.getId(), ConstraintSet.RIGHT, sectionConstraint.getId(), ConstraintSet.RIGHT);

                sectionSet.connect(sectionProgress.getId(), ConstraintSet.RIGHT, sectionProgressBar.getId(), ConstraintSet.RIGHT);
                sectionSet.connect(sectionProgress.getId(), ConstraintSet.LEFT, sectionProgressBar.getId(), ConstraintSet.LEFT);

                sectionSet.centerVertically(sectionProgress.getId(), sectionProgressBar.getId());
//                sectionSet.centerHorizontally(sectionProgress.getId(), sectionProgressBar.getId());
//                sectionSet.centerVertically(sectionProgressBar.getId(), sectionConstraint.getId());

                sectionSet.applyTo(sectionConstraint);
                sectionsList.addView(sectionConstraint);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Egzam View
        addExamButton();
    }

    private int getfinishedSub(int x) {
        int progress = 0;
        try {
            JSONArray sections = courseJSON.getJSONArray("sections");
            for(int i = 0; i < x; i++) {
                try {
                    JSONObject section = (JSONObject) sections.get(i);
                    JSONArray subsections = section.getJSONArray("subsections");
                    int a = (subsections.length()*100/allSubsections);

                    Log.i("PROGRES 1", "subsections.length()" + String.valueOf(a));
                    Log.i("PROGRES 1", "allSubsections" + String.valueOf(allSubsections));
                    Log.i("PROGRES AAA", String.valueOf(a));
                    int q = courseJSON.getInt("completed_percent");
                    Log.i("PROGRES QQQ", String.valueOf(q));
                    if(a <= q){
                        progress += a;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("PROGRES ====", String.valueOf(progress));
            return progress;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return progress;
    }

    public void addExamButton(){
        ConstraintLayout examButton = (ConstraintLayout) getLayoutInflater().inflate(R.layout.course_on_list, null);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 300);
        params.setMargins(40,50,40, 10);
        examButton.setLayoutParams(params);
        examButton.setBackgroundResource(R.drawable.round_background_grey);


        TextView exam = new TextView(CourseView.this);
        exam.setId(View.generateViewId());
        exam.setText("Egzamin");
        exam.setPadding(30,20,30,10);

        exam.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        exam.setTextColor(getResources().getColor(R.color.courseFontWhite));
        exam.setTextSize(20);

        TextView examInfo = new TextView(CourseView.this);
        examInfo.setId(View.generateViewId());
        examInfo.setText("Po przerobieniu kursu będziesz mógł przejść do rozwiązania testu.");
        examInfo.setPadding(30,20,30,10);

        examInfo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        examInfo.setTextColor(getResources().getColor(R.color.courseFontWhite));
        examInfo.setTextSize(15);


        examButton.setId(View.generateViewId());
        exam.setId(View.generateViewId());
        examInfo.setId(View.generateViewId());

        examButton.addView(exam);
        examButton.addView(examInfo);

        ConstraintSet examSet = new ConstraintSet();
        examSet.clone(examButton);
        examSet.connect(exam.getId(), ConstraintSet.LEFT, examButton.getId(), ConstraintSet.LEFT);
        examSet.connect(exam.getId(), ConstraintSet.TOP, examButton.getId(), ConstraintSet.TOP);
        examSet.connect(examInfo.getId(), ConstraintSet.LEFT, examButton.getId(), ConstraintSet.LEFT);
        examSet.connect(examInfo.getId(), ConstraintSet.TOP, exam.getId(), ConstraintSet.BOTTOM);

        examSet.applyTo(examButton);
        sectionsList.addView(examButton);
        try {
            if(Double.parseDouble(courseJSON.getString("completed_percent")) == 100.0){
                setExamAvaliable(examButton.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setExamAvaliable(int id){
        ConstraintLayout examButton = findViewById(id);
        examButton.setBackgroundResource(R.drawable.round_button_blue);
        examButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onclick 1");
                Toast.makeText(view.getContext(), "EGZAMIN???", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CourseView.this, ExamView.class);
                Bundle b = new Bundle();
                b.putString("courseId", courseID);
                intent.putExtras(b);

                startActivity(intent);
            }
        });
    }

}