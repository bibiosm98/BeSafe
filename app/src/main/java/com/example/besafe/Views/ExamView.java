package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ExamView extends AppCompatActivity {

    String courseId;
    JSONObject examResponse;
    int questionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Bundle b = getIntent().getExtras();
        courseId = b.getString("courseId");
        getExamData();
        setButtons();
    }

    public void setButtons() {
        Button button = findViewById(R.id.previevBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamView.super.onBackPressed();
            }
        });

        button = findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //POST data and start exam
                //get first question
                //LOAD new View
            }
        });
    }

    public void getExamData() {
        CourseRequest.getUserCourses(this, "user/test/" + courseId, new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.i("getExamData", result.toString(3));
                    setExamData(result.getJSONObject("response"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setExamData(JSONObject result) {
        examResponse = result;
        TextView textview;
        try {
            textview = findViewById(R.id.examName);
            textview.setText(result.getString("name"));

            textview = findViewById(R.id.description);
            textview.setText(result.getString("description"));

            textview = findViewById(R.id.testAttempts);
            textview.setText(result.getString("test_attempts"));

            textview = findViewById(R.id.maxTestAttempts);
            textview.setText(result.getString("max_test_attempts"));

            textview = findViewById(R.id.durationMinutes);
            textview.setText(result.getString("duration_minutes"));

            textview = findViewById(R.id.numberOfQuestions);
            textview.setText(result.getString("number_of_questions"));

            textview = findViewById(R.id.pointsToPass);
            textview.setText(result.getString("points_to_pass"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}