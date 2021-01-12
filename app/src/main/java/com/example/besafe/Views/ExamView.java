package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.besafe.R;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.Requests.ExamPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;

public class ExamView extends AppCompatActivity {

    String courseId;
    JSONObject examResponse;
    int questionNumber;
    String chosenAnswer;
    int allQuestion;
    int questionSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Bundle b = getIntent().getExtras();
        courseId = b.getString("courseId");
        questionNumber = 1;
        getExamData();
        setExamButtons();
    }

    public void setExamButtons() {
        Button button = findViewById(R.id.examLaterBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamView.super.onBackPressed();
            }
        });

        button = findViewById(R.id.examStartBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //POST data and start exam
                ExamPostRequest.examPost(ExamView.this, "user/test/" + courseId, new JSONObject(), new CourseRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            loadFirstQuestionView(result.getJSONObject("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void setTestButtons() {
        Button button = findViewById(R.id.previevBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("prevBTn", ""+(questionNumber-1));
                //questionNumber--;
                getQuestion(questionNumber-1);
            }
        });
        if(questionNumber==1){
            button.setVisibility(View.GONE);
        }

        button = findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("nextBTn", ""+(questionNumber+1));
                //questionNumber++;
                postAnswer();
                getQuestion(questionNumber+1);
            }
        });

        if(questionNumber==allQuestion){
            button.setVisibility(View.GONE);
            if(questionSend >= allQuestion-1){ // allQuestion get
                button.setVisibility(View.VISIBLE);
                button.setText("Zakończ egzamin");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishExam();
                    }
                });
            }
        }
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

    public void setExamData(JSONObject result) {
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
            allQuestion = Integer.parseInt(result.getString("number_of_questions"));

            textview = findViewById(R.id.pointsToPass);
            textview.setText(result.getString("points_to_pass"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getQuestion(int number) {
        CourseRequest.getUserCourses(this, "user/test/answer/" + courseId + "/" + number, new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
//                    Log.i("getQuestion", result.toString(3));
                    loadQuestionView(result.getJSONObject("response"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadFirstQuestionView(JSONObject response) {
        //GET DATA

//            Date currentTime = Calendar.getInstance().getTime();
//            Date time = Time.valueOf("13-12-2020 23:50:01");
//            time = time;
//            String currentTime2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
//            textView = findViewById(R.id.remainingTime);
//            textView.setText("Pozostały czas:" + time);

        try {
            loadQuestionView(response.getJSONObject("first_question"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadQuestionView(JSONObject response) {
        setContentView(R.layout.activity_exam_question_view);
        try {
//            if(response.getString("picture").equals("null")){
//                Log.i("EXAM", "No picture");
//                ConstraintLayout imageConstraint = findViewById(R.id.imageConstraint);
//                imageConstraint.removeAllViews();
//            }else{
//                Log.i("EXAM", "Set picture");
//                ImageView testImage = findViewById(R.id.testImage);
//                String url = "" + response.getString("picture");
//                CourseImageRequest.getImage(this, "", 0, new CourseImageRequest.VolleyCallback() {
//                    @Override
//                    public void onSuccess(Bitmap bitmap, int courseNumber) {
//                        addTestImage(bitmap);
//                    }
//                });
//
//            }
            TextView question = findViewById(R.id.question);
            question.setText(response.getString("question"));

            JSONArray answers = response.getJSONArray("answers");
            LinearLayout answersList = findViewById(R.id.answerListView);

            TextView questionNumberInfo = findViewById(R.id.questionNumber);
            questionNumber = Integer.parseInt(response.getString("order"));  // OR questionNumber++ in setTEstButton

            setTestButtons();
//            questionNumberInfo.setText("Pytanie " + questionNumber + " z " + (answers.length()+questionNumber));
            questionNumberInfo.setText("Pytanie " + questionNumber + " z " + allQuestion);

            for(int i=0; i<answers.length(); i++){
                final TextView answer = new TextView(this);

                answer.setId(View.generateViewId());
                answer.setText(answers.getString(i));
                answer.setTextSize(20);
                answer.setGravity(Gravity.CENTER_HORIZONTAL);
                answer.setPadding(10,40, 10,40);

                answer.setTextColor(getResources().getColor(R.color.courseFontBlue));
                answer.setBackgroundResource(R.drawable.round_background_white_border_blue);

                LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsExample.setMargins(40, 30, 40, 20);;
                answer.setLayoutParams(paramsExample);

                answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answer.setTextColor(getResources().getColor(R.color.courseFontWhite));
                        answer.setBackgroundResource(R.drawable.round_button_blue);
                        removeAllAnswers(answer.getId());
                    }
                });

                answersList.addView(answer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeAllAnswers(int id) {
        LinearLayout linear = findViewById(R.id.answerListView);
        for(int i=0; i<linear.getChildCount(); i++){

            if((linear.getChildAt(i).getId()) != id){
                ((TextView)linear.getChildAt(i)).setTextColor(getResources().getColor(R.color.courseFontBlue));
                ((TextView)linear.getChildAt(i)).setBackgroundResource(R.drawable.round_background_white_border_blue);
            }else{
                // set chosen Answer
                Log.i("ExamView", "answer" + i);
                chosenAnswer = (String) ((TextView) linear.getChildAt(i)).getText();
            }
        }
    }

    public void addTestImage(Bitmap bitmap) {
        ImageView image = new ImageView(ExamView.this);
        ConstraintLayout imageConstraint = findViewById(R.id.imageConstraint);
        imageConstraint.setBackgroundColor(getResources().getColor(R.color.courseFontWhite));
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
        imageConstraint.addView(image, 0);
    }

    public void postAnswer() {
        questionSend++;
        String link = "user/test/answer/" + courseId + "/" + questionNumber;
        Log.i("LINK", link);
        JSONObject body = new JSONObject();
        try {
            body.put("answer", chosenAnswer);

            Log.i("JSON", body.toString(3));
            ExamPostRequest.examPost(this, link, body, new CourseRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void finishExam() {
        CourseRequest.getUserCourses(this, "user/test/finish/" + courseId, new CourseRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    result = result.getJSONObject("response");
                    Log.i("FinishExam", result.toString(3));
//                    boolean passed = result.getBoolean("passed");
                    if(result.getBoolean("passed")){
                        setContentView(R.layout.exam_pass);

                    }else{
                        setContentView(R.layout.exam_failed);
                    }
                    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ExamView.this, UserCourses.class);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
//C A A A