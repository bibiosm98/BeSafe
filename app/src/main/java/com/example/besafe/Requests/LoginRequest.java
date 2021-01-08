package com.example.besafe.Requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.besafe.GlobalToken;

public class LoginRequest {

    final static String API = "https://bhpapi.herokuapp.com";

    public static void logIn(final Context context, String userEmail, String userPassword, final CourseRequest.VolleyCallback callback){
        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("Please Wait...");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        JSONObject object = new JSONObject();
        try {
            object.put("email", userEmail);
            object.put("password", userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API + "/api/login", object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON", String.valueOf(response));
                            loading.dismiss();
                            callback.onSuccess(response);
                            String Error = response.getString("httpStatus");
                            if (Error.equals("")){

                            }else if(Error.equals("OK")){
                                JSONObject body = response.getJSONObject("body");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                        }
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            e.printStackTrace();
                        }

                        Toast mToastToShow;
                        try {
                            json = (JSONObject) json.get("response");
                            GlobalToken.setTOKEN((String) json.get("token"));// = (String) json.get("token");
                            mToastToShow = Toast.makeText(context, "Token = " + GlobalToken.getTOKEN(), Toast.LENGTH_LONG);
                            mToastToShow.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
}

