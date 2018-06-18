package com.cvvid.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.Home;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.activities.candidate.ResetPassword;
import com.cvvid.activities.employer.EmployerProfile;
import com.cvvid.activities.institution.InstituteProfile;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class Signin extends AppCompatActivity {

    private TextView forgotpass;
    EditText email,password;
    Button login;
    AwesomeValidation validation;


    SharedPreferences sharedpreferences;
    private static final String TAG = "LoginActivity";
    public static final String MyPREFERENCES = "Login";
    private static final String URL_FOR_LOGIN = LOCATION_URL.LOGIN;
    ProgressDialog progressDialog;

    // Session Manager Class
    SessionManager session;

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getSupportActionBar().setTitle("Cvvid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE) ;


        forgotpass = (TextView)findViewById(R.id.forgotpass);
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
        login = (Button)findViewById(R.id.btn_action);

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        validation.addValidation(Signin.this, R.id.input_email,  Patterns.EMAIL_ADDRESS, R.string.emailerr);

        // to validate the confirmation of another field
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        validation.addValidation(Signin.this, R.id.input_password, RegexTemplate.NOT_EMPTY, R.string.passerr);

//        submit form
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               validation test
                if(validation.validate())
                {
                    submitForm(email.getText().toString(),password.getText().toString());
                }

            }
        });


        //forgot pass
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signin.this ,ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(Signin.this ,Home.class);
            startActivity(intent);
           // onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void submitForm(final String email, final String password)
    {
        progressDialog.setMessage("Please wait a moment");
        String cancel_req_tag = "login";
//            show progress bar
        showDialog();
        // Initialize a new StringRequest
        StringRequest strReq = new StringRequest(
                Request.Method.POST,URL_FOR_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("jobj---"+response);

                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);


                            int status = jObj.getInt("status");
                            String message = jObj.getString("message");

                            if(status == 200){
                                JSONArray jOb = jObj.getJSONArray("data");
                                JSONObject c = jOb.getJSONObject(0);

                                String type = c.getString("type");

                                if(type.equals("candidate")){

                                    String user_id = c.getString("id");
                                    String forenames = c.getString("forenames");
                                    String surname = c.getString("surname");
                                    String profile_id = c.getString("profile_id");

                                    String username = forenames+" "+surname;

                                    // Creating user login session
                                    // Use user real data
                                    session.createLoginSession(user_id, type, username, profile_id,"0", "0");

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();

                                    // Storing login value as TRUE
                                    editor.putBoolean(IS_LOGIN, true);
                                    editor.putString("user_id", user_id); // Storing string
                                    editor.putString("type", type); // Storing string
                                    editor.putString("username", username); // Storing string

                                    editor.commit(); // commit changes

                                    Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
                                    startActivity(intent);
                                }
                                else  if(type.equals("employer")){

                                    String user_id = c.getString("user_id");
                                    String forenames = c.getString("forenames");
                                    String surname = c.getString("surname");
                                    String profile_id = c.getString("profile_id");
                                    String emp_id = c.getString("id");

                                    String username = forenames+" "+surname;

                                    // Creating user login session
                                    // Use user real data
                                    session.createLoginSession(user_id, type, username, profile_id,emp_id,"0");

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();

                                    // Storing login value as TRUE
                                    editor.putBoolean(IS_LOGIN, true);
                                    editor.putString("user_id", user_id); // Storing string
                                    editor.putString("type", type); // Storing string
                                    editor.putString("username", username); // Storing string
                                    editor.putString("employer_id", emp_id);

                                    editor.commit(); // commit changes

                                    Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
                                    startActivity(intent);
                                }
                                else  if(type.equals("institution_admin")){

                                    String user_id = c.getString("user_id");
                                    String institution_id = c.getString("institution_id");
                                    // String forenames = c.getString("forenames");
                                    // String surname = c.getString("surname");

                                    String username = c.getString("name");

                                    // Creating user login session
                                    // Use user real data
                                    session.createLoginSession(user_id, type, username, "0","0", institution_id);

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("user_id", user_id); // Storing string
                                    editor.putString("type", type); // Storing string
                                    editor.putString("username", username); // Storing string
                                    editor.putString("institution_id", institution_id);
                                    editor.commit(); // commit changes

                                    Intent intent=new Intent(getApplicationContext(),InstituteProfile.class);
                                    startActivity(intent);

                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "Login failed. Incorrect credentials", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.print(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cvvid:cvvid123";
                String auth = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                // headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
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
