package com.cvvid.fragments.institute;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

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
public class IDetailsFragment extends Fragment {

    // Session Manager Class
    SessionManager session;

    ProgressDialog progressDialog;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String INSTITUTION_ID;

    private EditText input_website,input_name,input_fname,input_sname,input_email,input_currentjob;
    private Button btn_save;
    AwesomeValidation validation;

    public IDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_idetails, container, false);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        //input_dob = (EditText) view.findViewById(R.id.input_dob);
        input_name = (EditText) view.findViewById(R.id.input_name);
        input_website = (EditText) view.findViewById(R.id.input_website);
        input_fname = (EditText) view.findViewById(R.id.input_fname);
        input_sname = (EditText) view.findViewById(R.id.input_sname);
        input_email = (EditText) view.findViewById(R.id.input_email);
        input_currentjob = (EditText) view.findViewById(R.id.input_currentjob);
        btn_save = (Button) view.findViewById(R.id.btn_save);

//        session
        session = new SessionManager(getContext());


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        INSTITUTION_ID = user.get(SessionManager.INSTITUTION_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        //save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = input_name.getText().toString();
                String website = input_website.getText().toString();
                String forenames = input_fname.getText().toString();
                String surnames = input_sname.getText().toString();
                String email = input_email.getText().toString();
                String currentjob = input_currentjob.getText().toString();

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);

                validation.addValidation(getActivity(), R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.schnameerr);
                validation.addValidation(getActivity(), R.id.input_fname, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
                validation.addValidation(getActivity(), R.id.input_sname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
                validation.addValidation(getActivity(), R.id.input_email, Patterns.EMAIL_ADDRESS, R.string.inemail);

                if(validation.validate())
                {
                    saveChanges(INSTITUTION_ID,name, forenames, surnames, email, currentjob, website);
                }
            }
        });


        getLoadData(USER_ID, view);


        return view;
    }


    private void saveChanges(final String USER_ID, final String name, final String forenames, final String surnames, final String email, final String currentjob, final String website)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("loading...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/updateinstitutionbasicdetail/id/" + USER_ID;

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
                params.put("name", name);
                params.put("forenames", forenames);
                params.put("surname", surnames);
                params.put("website", website);
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

        String URL_FOR_DETAIL = CONFIG_URL+"/getinstitutions/id/" + USER_ID;

        System.out.println("WEB=="+URL_FOR_DETAIL);

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

                    EditText name = (EditText) view.findViewById(R.id.input_name);
                    name.setText(c.getString("name"));
                    EditText fname = (EditText) view.findViewById(R.id.input_fname);
                    fname.setText(c.getString("forenames"));
                    EditText sname = (EditText) view.findViewById(R.id.input_sname);
                    sname.setText(c.getString("surname"));
                    EditText website = (EditText) view.findViewById(R.id.input_website);
                    website.setText(c.getString("website"));
                    EditText email = (EditText) view.findViewById(R.id.input_email);
                    email.setText(c.getString("email"));
                    EditText current_job = (EditText) view.findViewById(R.id.input_currentjob);
                    current_job.setText(c.getString("current_job"));

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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


}
