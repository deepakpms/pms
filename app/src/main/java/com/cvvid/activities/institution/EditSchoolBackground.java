package com.cvvid.activities.institution;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.activities.employer.EditCompanyBackground;
import com.cvvid.activities.employer.EmployerProfile;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_EDIT;
import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_GET;
import static com.cvvid.apicall.LOCATION_URL.INSTITUTE_BACKGROUND_EDIT;
import static com.cvvid.apicall.LOCATION_URL.SCHOOL_BACKGROUND_GET;

public class EditSchoolBackground extends AppCompatActivity {

    String cbid,INS_ID;
    private EditText background;

    // LogCat tag
    private static final String TAG = EditSchoolBackground.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";

    SessionManager session;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_school_background);

        Button btn_update = (Button) findViewById(R.id.btn_update);
        background = (EditText) findViewById(R.id.edit_background);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //        session
        session = new SessionManager(EditSchoolBackground.this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        Intent ie = getIntent();
        cbid = ie.getStringExtra("cbid");
        INS_ID = user.get(SessionManager.INSTITUTION_ID);
        setData();

        // Receiving the data from previous activity
        Intent i = getIntent();

        /**
         * back video button click event
         */
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Choose video
                sendFragment();
            }
        });

        /**
         * save video button click event
         */
        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String cb = background.getText().toString();
                saveBackground(cb);
            }
        });
    }

    public void saveBackground(final String backgr)
    {
        String cancel_req_tag = "background";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = INSTITUTE_BACKGROUND_EDIT + INS_ID;

//        System.out.println("YRR=="+backgr);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "save hobby Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        sendFragment();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("body", backgr);
                return params;

            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }
    private void setData()
    {

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Fetching..");

        showDialog();

        String url = SCHOOL_BACKGROUND_GET+INS_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");


                    JSONObject c = jUser.getJSONObject(0);

                    String act = c.getString("body");

                    background.setText(Html.fromHtml(act));



                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(EditSchoolBackground.this,error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(this).addToRequestQueue(strReq, cancel_req_tag);
    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),InstituteProfile.class);
        intent.putExtra("position","1");
        intent.putExtra("issetting","0");
        startActivity(intent);

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
