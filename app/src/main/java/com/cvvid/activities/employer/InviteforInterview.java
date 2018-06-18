package com.cvvid.activities.employer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.Signin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class InviteforInterview extends AppCompatActivity {

    private EditText interview_date,time,message;
    public static Integer dday,dmonth,dyear,thours,tminutes;
    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;

    private static final String URL_FOR_LOGIN = LOCATION_URL.INVITE_INTERVIEW;

    AwesomeValidation validation;


    private String jobid,jobapplicationid;

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitefor_interview);

        interview_date = (EditText)findViewById(R.id.interview_date);
        time = (EditText)findViewById(R.id.interview_time);
        message = (EditText)findViewById(R.id.message);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        Bundle extras = getIntent().getExtras();
        jobapplicationid = extras.getString("jobapplicationid");
        jobid = extras.getString("jobid");


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                dday=dayOfMonth;
                dmonth=monthOfYear;
                dyear=year;
                updateLabel();
                updateDisplay();
            }


        };

        interview_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {

                    new DatePickerDialog(InviteforInterview.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch (Exception e)
                {
                    System.out.println("ERROR"+e.getLocalizedMessage());
                }

            }
        });

        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InviteforInterview.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String AM_PM ;
                        if(selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }


                        thours = selectedHour;
                        tminutes = selectedMinute;

                        time.setText( selectedHour + ":" + selectedMinute + ":" +"00 "+ AM_PM);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button btn_invite = (Button)findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String invitedate = dyear+"-"+dmonth+"-"+dday+" "+thours+":"+tminutes+":00";
                String mess = message.getText().toString();

                validation.addValidation(InviteforInterview.this, R.id.message, RegexTemplate.NOT_EMPTY, R.string.meserr);

                if(validation.validate())
                {
                    submitForm(invitedate,mess);
                }
              //  String forenames = input_fname.getText().toString();
              //  String surnames = input_sname.getText().toString();

            }
        });


    }

    private void submitForm(final String date, final String message)
    {
        progressDialog.setMessage("Please wait a moment");
        String cancel_req_tag = "login";
//            show progress bar
        showDialog();
        // Initialize a new StringRequest
        StringRequest strReq = new StringRequest(
                Request.Method.POST,URL_FOR_LOGIN +jobapplicationid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("interview---"+response);

                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);


                            int status = jObj.getInt("status");
                            String message = jObj.getString("message");
                            String data = jObj.getString("data");

                            if(status == 200)
                            {
                                Intent i = new Intent(InviteforInterview.this, ShortlistApplications.class);
                                i.putExtra("jobid", jobid);
                                startActivity(i);

                                Toast.makeText(InviteforInterview.this, data, Toast.LENGTH_LONG).show();

                            }
                            else{
                                Toast.makeText(InviteforInterview.this, message, Toast.LENGTH_LONG).show();
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
                params.put("interview_date", date);
                params.put("msg", message);
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


    private void updateLabel() {
        interview_date.setText(dday + "/" + (dmonth +1)  + "/" + dyear);
    }

    public void updateDisplay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
     //   age = year - dyear;
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
