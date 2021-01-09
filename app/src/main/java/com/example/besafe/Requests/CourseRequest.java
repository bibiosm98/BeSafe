package com.example.besafe.Requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.besafe.GlobalToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CourseRequest extends AppCompatActivity {
    final static String TAG = "AllCourseRequest";
    final static String API = "https://bhpapi.herokuapp.com/api/";

    public static void getUserCourses(final Context context, String link, final VolleyCallback callback){
        Log.i(TAG, "Requests: UserCourseRequest");

        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("Please Wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JsonObjectRequest userCourses = new JsonObjectRequest(Request.Method.GET, API+link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", GlobalToken.getTOKEN());
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(userCourses);
    }
    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }
}
