package com.cvvid.activities.employer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.activities.candidate.AddDocument;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.AndroidMultiPartEntity;
import com.cvvid.common.ImageFilePath;
import com.cvvid.common.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_EDIT;
import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_GET;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EditCompanyBackground extends AppCompatActivity {

    private ProgressBar progressBar;
    Context context;
    long totalSize = 0;
    String imagepath;
    String cbid,EMP_ID;
    private TextView txtPercentage;
    private EditText background;
    private String filePath = null;
    private String isFile = null;
    private JSONArray result;
    private ArrayList<String> categoryarray;
    MaterialSpinner scategory;
    String filename;
    private ArrayList<String> publisharray;
    MaterialSpinner spublish;

    // LogCat tag
    private static final String TAG = EditCompanyBackground.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;
    SessionManager session;
    private TextView displayname;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_background);

        Button btn_update = (Button) findViewById(R.id.btn_update);
        background = (EditText) findViewById(R.id.edit_background);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //        session
        session = new SessionManager(EditCompanyBackground.this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        Intent ie = getIntent();
        cbid = ie.getStringExtra("cbid");
        EMP_ID = user.get(SessionManager.EMPLOYER_ID);
        setData();

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity


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

        String url  = EMPLOYER_BACKGROUND_EDIT + EMP_ID;

        System.out.println("YRR=="+backgr);
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

        String url = EMPLOYER_BACKGROUND_GET+EMP_ID;

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
                Toast.makeText(EditCompanyBackground.this,error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(this).addToRequestQueue(strReq, cancel_req_tag);
    }


    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //send fragment
                        sendFragment();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
        intent.putExtra("position","2");
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
