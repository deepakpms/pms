package com.cvvid.fragments.candidate;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateUpgrade;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.cvvid.common.Signin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CSecurityFragment extends Fragment {

    SwitchCompat switchCompat, profile_upgrade,hiredswitch;
    AwesomeValidation validation;

    private String visiblity = "0";
    private String hired = "0";

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private EditText input_currentpass,input_password,input_cpass;
    private Button btn_save;

    ProgressDialog progressDialog;


    public CSecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_csecurity, container, false);

        switchCompat = (SwitchCompat)view.findViewById(R.id.switch_id);
        profile_upgrade = (SwitchCompat)view.findViewById(R.id.profile_upgrade);
        hiredswitch = (SwitchCompat)view.findViewById(R.id.hired_switch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    visiblity = "1";
                } else {
                    visiblity = "0";
                }
            }
        });

        profile_upgrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent i = new Intent(getActivity(), CandidateUpgrade.class);
                    startActivity(i);

                } else {
                    profile_upgrade.setChecked(false);
                }
            }
        });

        hiredswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hired = "1";
                } else {
                    hired = "0";
                }

                changeHired(hired);
            }
        });
        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

        input_currentpass = (EditText) view.findViewById(R.id.input_currentpass);
        input_password = (EditText) view.findViewById(R.id.input_password);
        input_cpass = (EditText) view.findViewById(R.id.input_cpass);
        btn_save = (Button) view.findViewById(R.id.btn_save);



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

        getLoadData(USER_ID, view);

        //save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String oldpassword = input_currentpass.getText().toString();
                String password = input_password.getText().toString();
                String cpassword = input_cpass.getText().toString();

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);

                validation.addValidation(getActivity(), R.id.input_currentpass,  RegexTemplate.NOT_EMPTY, R.string.oldpassr);
                validation.addValidation(getActivity(), R.id.input_password,  RegexTemplate.NOT_EMPTY, R.string.passerr);
                validation.addValidation(getActivity(), R.id.input_cpass, R.id.input_password, R.string.cpasserr);

                if(validation.validate())
                {
                    saveChanges(USER_ID, oldpassword, password, visiblity);
                }


            }
        });



        return view;
    }

    private void saveChanges(final String USER_ID, final String oldpassword, final String password, final String visiblity)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("loading...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/updatepassword/id/" + USER_ID;

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
                    String data = jObj.getString("data");
                    if(status == 200)
                    {
                        Toast.makeText(getContext(),"Successfully Saved", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getContext(), Signin.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(getContext(),data, Toast.LENGTH_LONG).show();
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
                params.put("current_password", oldpassword);
                params.put("new_password", password);
                params.put("visibility", visiblity);
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

        String URL_FOR_DETAIL = "http://pdev.work/cvvidapi/api/fetchcandidatedetails/id/" + USER_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Responsejsbjb: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    if(c.getString("visibility").equals("0"))
                    {
                        switchCompat.setChecked(false);
                    }else{
                        switchCompat.setChecked(true);
                    }
                    if(c.getString("hired").equals("0"))
                    {
                        hiredswitch.setChecked(false);
                    }else{
                        hiredswitch.setChecked(true);
                    }
                    if(c.getString("stripe_active").equals("1"))
                    {
//                        profile_upgrade.setChecked(true);
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

    private void changeHired(final String hired)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("updating...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/candidatehiredstatus/id/" + USER_ID;

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
                    String data = jObj.getString("data");
                    if(status == 200)
                    {
                        Toast.makeText(getContext(),"Successfully updated", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(),data, Toast.LENGTH_LONG).show();
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

                params.put("hired", hired);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
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
