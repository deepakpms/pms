package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.APPLY_JOB;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class JobDescription extends AppCompatActivity {


    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;

    ProgressDialog progressDialog;

    Button apply_job;
    TextView btn_back;
    Boolean status = false;

    private String job_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);

        // Progress dialog
        progressDialog = new ProgressDialog(JobDescription.this);
        progressDialog.setCancelable(false);

        apply_job = (Button)findViewById(R.id.apply_job);
        btn_back = (TextView) findViewById(R.id.btn_back);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
            job_id =  getIntent().getExtras().getString("job_id");


        //        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        isUpgradeCheck();

        getLoadData();


        apply_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyjob();
            }
        });

        System.out.println("----------back----------");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void isUpgradeCheck() {

        String URL = LOCATION_URL.CANDIDATE_DETAIL + USER_ID;

        getResponse(Request.Method.GET, URL, null,
                new JobDescription.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {
                            JSONObject jarray = new JSONObject(result);

                            JSONArray jUser = jarray.getJSONArray("data");

                            JSONObject c = jUser.getJSONObject(0);

                            String upgrade = c.getString("upgrade");

                            if(upgrade.equals("1"))
                            {
                                status = true;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }


    public void getResponse(int method, String url, JSONObject jsonValue, final JobDescription.VolleyCallback callback) {

        String cancel_req_tag = "cancel";

        RequestQueue queue = AppSingleton.getInstance(this).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(JobDescription.this, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(this).addToRequestQueue(strreq, cancel_req_tag);
    }


    private void applyjob()
    {

        String cancel_req_tag = "Apply Job";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = APPLY_JOB + USER_ID;
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
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        apply_job.setText("Already Applied");
                        apply_job.setEnabled(false);
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
                params.put("job_id", job_id);
                return params;

            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    public void getLoadData() {

        String URL = CONFIG_URL+"/getjob/id/" + job_id+"/user_id/"+USER_ID;

        getResponse(Request.Method.GET, URL, null,
                new JobDescription.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        Log.d(TAG, "Job Response: " + result.toString());
                        try {
                            JSONObject jarray = new JSONObject(result);

                            JSONArray jUser = jarray.getJSONArray("data");

                            JSONObject c = jUser.getJSONObject(0);


                            String salary_type = c.getString("salary_type");

                            String stype="";
                            if(salary_type == "annual")
                                stype = "annum";
                            else
                                stype = "hour";

                            TextView industryname = (TextView)findViewById(R.id.industryname);
                            industryname.setText(c.getString("name"));
                            TextView jobtitle = (TextView)findViewById(R.id.jobtitle);
                            jobtitle.setText(c.getString("title"));
                            TextView locationt = (TextView)findViewById(R.id.location);
                            locationt.setText(c.getString("location"));
                            TextView salary = (TextView)findViewById(R.id.salary);
                            salary.setText("£ "+c.getString("salary_min")+" to £ "+c.getString("salary_max")+" per "+stype);
                            TextView descriptiont = (TextView)findViewById(R.id.description);
                            descriptiont.setText(c.getString("description"));

                            if(status)
                            {
                                apply_job.setEnabled(true);
                            }

                            if(!c.getString("jobapplication").equals("")) {
                                Button apply_job = (Button) findViewById(R.id.apply_job);
                                apply_job.setText("Already Applied");
                                apply_job.setEnabled(false);
                            }else {
                                apply_job.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
