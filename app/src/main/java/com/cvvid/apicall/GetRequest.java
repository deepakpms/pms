package com.cvvid.apicall;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class GetRequest {

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }


    public void getResponse(int method, String url, JSONObject jsonValue, final GetRequest.VolleyCallback callback, Context context) {

        String cancel_req_tag = "cancel";

        RequestQueue queue = AppSingleton.getInstance(context).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                //Toast.makeText(context, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(context).addToRequestQueue(strreq, cancel_req_tag);
    }
}
