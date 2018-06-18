package com.cvvid.fragments.employer;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateUpgrade;
import com.cvvid.activities.employer.EmployerUpgrade;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class EDetailsFragment extends Fragment {

    // Session Manager Class
    SessionManager session;
    SwitchCompat profile_upgrade;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    public static Integer dday,dmonth,dyear;
    Calendar myCalendar = Calendar.getInstance();
    public static int age;
    public String rdob;
    private EditText input_dob,input_fname,input_sname,input_email,input_currentjob;
    private Button btn_save;
    AwesomeValidation validation;

    ProgressDialog progressDialog;


    public EDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edetails, container, false);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

        input_dob = (EditText) view.findViewById(R.id.input_dob);
        input_fname = (EditText) view.findViewById(R.id.input_fname);
        input_sname = (EditText) view.findViewById(R.id.input_sname);
        input_email = (EditText) view.findViewById(R.id.input_email);
        input_currentjob = (EditText) view.findViewById(R.id.input_currentjob);
        profile_upgrade = (SwitchCompat)view.findViewById(R.id.profile_upgrade);
        btn_save = (Button) view.findViewById(R.id.btn_save);

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

        input_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR)-16, myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

//        employer upgrade
        profile_upgrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent i = new Intent(getActivity(), EmployerUpgrade.class);
                    startActivity(i);

                } else {
                    profile_upgrade.setChecked(false);
                }
            }
        });

//save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String forenames = input_fname.getText().toString();
                String surnames = input_sname.getText().toString();
                String email = input_email.getText().toString();
                String currentjob = input_currentjob.getText().toString();
                String dob = dyear+"-"+dmonth+"-"+dday;

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);

                validation.addValidation(getActivity(), R.id.input_fname, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
                validation.addValidation(getActivity(), R.id.input_sname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
                validation.addValidation(getActivity(), R.id.input_email, Patterns.EMAIL_ADDRESS, R.string.inemail);
                 // to validate with a simple custom validator function
                validation.addValidation(getActivity(), R.id.input_dob, new SimpleCustomValidation() {
                    @Override
                    public boolean compare(String input) {
                        // check if the age is >= 18
                        try {
                            Calendar calendarBirthday = Calendar.getInstance();
                            Calendar calendarToday = Calendar.getInstance();
                            calendarBirthday.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(input));
                            int yearOfToday = calendarToday.get(Calendar.YEAR);
                            int yearOfBirthday = calendarBirthday.get(Calendar.YEAR);
                            if (yearOfToday - yearOfBirthday > 16) {
                                return true;
                            } else if (yearOfToday - yearOfBirthday == 16) {
                                int monthOfToday = calendarToday.get(Calendar.MONTH);
                                int monthOfBirthday = calendarBirthday.get(Calendar.MONTH);
                                if (monthOfToday > monthOfBirthday) {
                                    return true;
                                } else if (monthOfToday == monthOfBirthday) {
                                    if (calendarToday.get(Calendar.DAY_OF_MONTH) >= calendarBirthday.get(Calendar.DAY_OF_MONTH)) {
                                        return true;
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }, R.string.doberr);

                if(validation.validate())
                {
                    saveChanges(USER_ID, forenames, surnames, email, currentjob, dob);
                }


              //  saveChanges(USER_ID, forenames, surnames, email, currentjob, dob);
            }
        });



        getLoadData(USER_ID, view);

        return view;
    }

    private void saveChanges(final String USER_ID, final String forenames, final String surnames, final String email, final String currentjob, final String dob)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("loading...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/updatebasicdetail/id/" + USER_ID;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        Toast.makeText(getContext(),"Successfully Saved", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("forenames", forenames);
                params.put("surname", surnames);
                params.put("dob", dob);
                params.put("email", email);
                params.put("current_job", currentjob);

                return params;

            }



        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void getLoadData(final String USER_ID, final View view)
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = CONFIG_URL+"/fetchemployerprofiledetails/id/" + USER_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    EditText fname = (EditText) view.findViewById(R.id.input_fname);
                    fname.setText(c.getString("forenames"));
                    EditText sname = (EditText) view.findViewById(R.id.input_sname);
                    sname.setText(c.getString("surname"));
                    EditText dob = (EditText) view.findViewById(R.id.input_dob);
                    dob.setText(c.getString("dob"));
                    EditText email = (EditText) view.findViewById(R.id.input_email);
                    email.setText(c.getString("email"));
                    EditText current_job = (EditText) view.findViewById(R.id.input_currentjob);
                    current_job.setText(c.getString("current_job"));

                    if(c.getString("stripe_active").equals("1"))
                    {
                        profile_upgrade.setClickable(false);
                        profile_upgrade.setText("Already Upgraded");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);

    }




    private void updateLabel() {
        input_dob.setText(dday + "/" + (dmonth +1)  + "/" + dyear);
    }

    public void updateDisplay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        age = year - dyear;
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
