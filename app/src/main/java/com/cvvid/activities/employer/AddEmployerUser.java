package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.CREATE_EMPLOYER_USER;

public class AddEmployerUser extends AppCompatActivity {

    String imagepath;
    File sourceFile;
    long totalSize = 0;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private String filePath = null;
    private String isFile = null;
    Context context;
    private static final String URL_CHECK_EMAIL = "http://cvvid.com/api/api/EmailVerify";
    private JSONArray result;

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "CVVid";

    // LogCat tag
    private static final String TAG = AddEmployerUser.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private TextView displayname;

    private Uri fileUri; // file url to store image/video

    AwesomeValidation validation;


    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;

    EditText forename_field,surname_field,email_field,pwd_field,cpwd_field;
    String fname,sname,email,pwd,cpwd;
    String USER_ID,EMP_ID;
    // Session Manager Class
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_euser);

        Button btn_save = (Button) findViewById(R.id.btn_save_euser);
        displayname = (TextView) findViewById(R.id.displayname);

        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE) ;

        forename_field = (EditText)findViewById(R.id.forename);
        surname_field = (EditText) findViewById(R.id.surname);
        email_field = (EditText) findViewById(R.id.email);
        pwd_field = (EditText) findViewById(R.id.password);
        cpwd_field = (EditText) findViewById(R.id.confirm_password);
//        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // emp_id
        EMP_ID = user.get(SessionManager.EMPLOYER_ID);

        email_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    checkValidEmail();
                }
            }
        });

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        validation.addValidation(AddEmployerUser.this, R.id.forename, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
        validation.addValidation(AddEmployerUser.this, R.id.surname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
        validation.addValidation(AddEmployerUser.this, R.id.email, RegexTemplate.NOT_EMPTY, R.string.emailerr);

        // to validate the confirmation of another field
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        validation.addValidation(AddEmployerUser.this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.passerr);
        validation.addValidation(AddEmployerUser.this, R.id.confirm_password, R.id.input_password, R.string.cpasserr);

        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendFragment();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validation.validate())
                {
                    fname = forename_field.getText().toString();
                    sname = surname_field.getText().toString();
                    email = email_field.getText().toString();
                    pwd = pwd_field.getText().toString();
                    cpwd = cpwd_field.getText().toString();
                    saveEmployerUser(fname,sname,email,pwd,cpwd);
                }

            }
        });


    }

    public void saveEmployerUser(final String fname,final String sname,final String email,final String pwd, final String cpwd)
    {
        String cancel_req_tag = "employer user";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = CREATE_EMPLOYER_USER + EMP_ID;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "save employer user Response: " + response.toString());
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
                params.put("forenames", fname);
                params.put("surname", sname);
                params.put("email", email);
                params.put("password", pwd);
                return params;

            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
        intent.putExtra("issetting","0");
        intent.putExtra("position","3");
        startActivity(intent);

    }
    public void checkValidEmail() {

        String cancel_req_tag = "Verify Email";
        progressDialog.setMessage("Checking email Availability..");
        showDialog();

        //Creating a string request

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_CHECK_EMAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status != 200)
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
                params.put("email", email_field.getText().toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

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
