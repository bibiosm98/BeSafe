package com.example.besafe.Requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.besafe.GlobalToken;

import java.util.HashMap;
import java.util.Map;

public class CourseImageRequest {
    static final String TAG = "ImageRequest";

    public static void getImage(final Context context, String url, final int courseNumber, final CourseImageRequest.VolleyCallback callback){
        com.android.volley.toolbox.ImageRequest request = new com.android.volley.toolbox.ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        callback.onSuccess(bitmap, courseNumber);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Image error", error.toString());

                    }
                }){
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
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
//            @Override
//            public void addMarker(String tag) {
//                super.addMarker(tag);
//                if (tag.equals("cache-hit")){
//                    Log.i(" CACHE HIT ", " CACHE HIT  CACHE HIT  CACHE HIT  CACHE HIT  CACHE HIT  CACHE HIT  CACHE HIT ");
//                }
//            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }
    public interface VolleyCallback{
        void onSuccess(Bitmap bitmap, int courseNumber);
    }
}
