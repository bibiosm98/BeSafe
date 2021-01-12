package com.example.besafe.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.besafe.GlobalToken;
import com.example.besafe.R;
import com.example.besafe.Requests.CourseImageRequest;
import com.example.besafe.Requests.CourseRequest;
import com.example.besafe.UserBottomMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CourseSection extends AppCompatActivity {
    final String TAG = "Course Sections";

    JSONObject courseJSON;
    LinearLayout courseSections;
    String courseId;
    String courseSection;
    int courseSubsection;
    int subsectionCount;

    int ID;

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

//                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 50);
//                        params.setMargins(50,150,50, 50);
//                        textView.setLayoutParams(params);
                        textView.setText(caption);
                        textView.setPadding(0,10,0,0);

                        subsectionView.setBackground(ContextCompat.getDrawable(this, R.drawable.round_course_gradient_blue));
//                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 600);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//                        params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 500);
                        params.setMargins(40,50,40, 60);
                        subsectionView.setLayoutParams(params);

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

//                        subsectionView.setBackground(ContextCompat.getDrawable(this, R.drawable.round_course_gradient_blue));
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 600);
                        params.setMargins(40,50,40, 60);
                        subsectionView.setLayoutParams(params);


                        String caption = ((JSONObject) subsections.get(i)).getString("caption");
                        textView.setText(caption);
                        textView.setPadding(0,10,0,0);

                        subsectionView.addView(textView, 0);
                        courseSections.addView(subsectionView);

                        ID = i;
                        Log.i("ID 1 =", String.valueOf(ID));
//                        CourseRequest.getUserCourses(this, "courses/" + courseId + "/" + ((JSONObject) subsections.get(i)).getString("data"), new CourseRequest.VolleyCallback() {
                        CourseRequest.getUserCourses(this, "courses/" + "bhp1" + "/" + ((JSONObject) subsections.get(i)).getString("data"), new CourseRequest.VolleyCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                Log.i("ID 2 =", String.valueOf(ID));
//                                ConstraintLayout imageView = (ConstraintLayout) courseSections.getChildAt(subsectionNumber);
                                ConstraintLayout videoViewConstraint = (ConstraintLayout) courseSections.getChildAt(ID);
                                addVideo(videoViewConstraint, result);
                            }
                        });
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            addNextButton();
        }
    }
    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            Log.i(TAG, "MEDIA NAME IF = " + Uri.parse(mediaName));

            return Uri.parse(mediaName).buildUpon().build();
        } else {
            Log.i(TAG, "MEDIA NAME ELSE = " + Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName));
            return Uri.parse("android.resource://" + getPackageName() + "/raw/" + mediaName);
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
        imageViewConstraint.setBackground(ContextCompat.getDrawable(this, R.drawable.round_edittext_white));

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

    public void addVideo(ConstraintLayout videoViewConstraint, JSONObject result){
////
////        result.toString(3);
//        VideoView videoView = findViewById(R.id.videoView);
        VideoView videoView = new VideoView(CourseSection.this);
        MediaController controller = new MediaController(CourseSection.this);
        MediaPlayer mp = new MediaPlayer();
//
//        Uri videoUri = null;
////        videoUri = getMedia("https://developers.google.com/training/images/tacoma_narrows.mp4");
////        videoUri = getMedia("https://bhpserver.blob.core.windows.net/bhp1/vid1.mp4?se=2020-12-05T21%3A53%3A34Z&sp=rt&sv=2020-02-10&sr=b&sig=8uavVeC0Ihgk3A8kjT3EetbaCKDQLthhVF4fRqCbWcM%3Dtype=video/mp4");
////        videoView.setVideoURI(videoUri);
//        try {
//            videoUri = getMedia(result.getString("response"));
////            Map<String, String> headers = new HashMap<>();
////            headers.put("token", GlobalToken.getTOKEN());
////            videoView.setVideoURI(videoUri, headers);
//
//            mp.setDataSource(this, videoUri);
            controller.setMediaPlayer(videoView);
            videoView.setMediaController(controller);
//            videoView.setVideoURI(videoUri);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
////        videoView.setVideoURI(videoUri);


        Uri video = null;
//        try {
//            video = Uri.parse(result.getString("response"));
//            video = Uri.parse("https://developers.google.com/training/images/tacoma_narrows.mp4");
//            videoView.setVideoURI(video);
////            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                @Override
////                public void onPrepared(MediaPlayer mp) {
////                    mp.setLooping(true);
////                    videoView.start();
////                }
////            });
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//        videoView.setMediaController(new MediaController(this));
//        videoView.setVideoURI(Uri.parse("https://developers.google.com/training/images/tacoma_narrows.mp4"));
//        videoView.requestFocus();
//        videoView.start();


        String LINK = null;
        try {
            LINK = result.getString("response");
//            LINK = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        video = Uri.parse(LINK);
        videoView.setMediaController(mc);
        videoView.setVideoURI(video);
        videoView.start();


//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 800);
        params.setMargins(40,50,40, 60);
        videoView.setLayoutParams(params);

        TextView videoCaption = (TextView) videoViewConstraint.getChildAt(0);
        videoCaption.setId(View.generateViewId());
        videoView.setId(View.generateViewId());
        videoViewConstraint.setId(View.generateViewId());
//        videoViewConstraint.setBackground(ResourcesCompat.getDrawable(null, R.drawable.round_edittext_white, null));



        ConstraintSet set = new ConstraintSet();
        set.clone(videoViewConstraint);
        set.connect(videoView.getId(), ConstraintSet.LEFT, videoViewConstraint.getId(), ConstraintSet.LEFT);
        set.connect(videoView.getId(), ConstraintSet.RIGHT, videoViewConstraint.getId(), ConstraintSet.RIGHT);
//        set.connect(video.getId(), ConstraintSet.TOP, videoViewConstraint.getId(), ConstraintSet.TOP);
//        set.connect(video.getId(), ConstraintSet.BOTTOM, videoViewConstraint.getId(), ConstraintSet.BOTTOM);


        set.connect(videoCaption.getId(), ConstraintSet.RIGHT, videoViewConstraint.getId(), ConstraintSet.RIGHT);
        set.connect(videoCaption.getId(), ConstraintSet.LEFT, videoViewConstraint.getId(), ConstraintSet.LEFT);
        set.connect(videoCaption.getId(), ConstraintSet.BOTTOM, videoViewConstraint.getId(), ConstraintSet.BOTTOM);

        set.applyTo(videoViewConstraint);
        videoViewConstraint.addView(videoView);
    }
    public void addNextButton(){
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
        set.connect(buttonId, ConstraintSet.RIGHT, buttonConstraintId, ConstraintSet.RIGHT);
        set.applyTo(nextButtonConstraint);

        ConstraintSet set2 = new ConstraintSet();
        set2.clone(nextButton);
        set2.connect(buttonTextId, ConstraintSet.LEFT, buttonId, ConstraintSet.LEFT);
        set2.connect(buttonTextId, ConstraintSet.RIGHT, buttonId, ConstraintSet.RIGHT);
        set2.connect(buttonTextId, ConstraintSet.TOP, buttonId, ConstraintSet.TOP);
        set2.connect(buttonTextId, ConstraintSet.BOTTOM, buttonId, ConstraintSet.BOTTOM);
        set2.applyTo(nextButton);



    }
}