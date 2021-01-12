package com.example.besafe.Requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.besafe.GlobalToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExamPostRequest {
    final static String TAG = "ExamPostRequest";
    final static String API = "https://bhpapi.herokuapp.com/api/";

    public static void examPost(final Context context, String link, final JSONObject body, final CourseRequest.VolleyCallback callback){
        Log.i(TAG, "Requests: ExamPostRequest");

        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("Please Wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JsonObjectRequest userCourses = new JsonObjectRequest(Request.Method.POST, API+link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i(TAG, response.toString(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

            @Override
            public byte[] getBody() {
                Log.i("POST JSON", " getBody PARAMS");
                HashMap<String, String> params2 = new HashMap<String, String>();
                try {
                    params2.put("answer", body.getString("answer"));
                    return new JSONObject(params2).toString().getBytes();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return super.getBody();
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(userCourses);
    }
    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }
}
