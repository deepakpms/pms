package com.cvvid.fragments.employer;


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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateUpgrade;
import com.cvvid.activities.employer.EmployerBasicRegister;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.common.Signin;
import com.cvvid.fragments.candidate.CBusinessCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ESecurityFragment extends Fragment {

    SwitchCompat switchCompat, profile_upgrade;
    AwesomeValidation validation;

    private String visiblity = "0";

    // Session Manager Class
    SessionManager session;

    private String USER_ID,EMP_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private EditText input_comp,input_addr,input_web;
    private MaterialSpinner input_ind;
    private Button btn_save;
    private ArrayList<String> industryarray;
    public static String industryList="";
    public static String industry_id="";
    private JSONArray result;
    HashMap<String ,String> indmap_get_index,indmap_get_id;
    ProgressDialog progressDialog;
    Integer industryid;

    public ESecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emp_comp, container, false);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

        input_comp = (EditText) view.findViewById(R.id.input_company);
        input_addr = (EditText) view.findViewById(R.id.input_address);
        input_web = (EditText) view.findViewById(R.id.input_website);
        input_ind = (MaterialSpinner) view.findViewById(R.id.industry);
        indmap_get_index = new HashMap<String,String>();
        indmap_get_id = new HashMap<String,String>();
        industryarray = new ArrayList<String>();

        getIndustry();
//        myWebServiceFun();
        btn_save = (Button) view.findViewById(R.id.btn_save);



        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);
        // emp_id
        EMP_ID = user.get(SessionManager.EMPLOYER_ID);
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        getLoadData(EMP_ID, view);

        //save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String comp = input_comp.getText().toString();
                String addr = input_addr.getText().toString();
                String web = input_web.getText().toString();
                String ind = input_ind.getSelectedItem().toString();

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);

                validation.addValidation(getActivity(), R.id.input_currentpass,  RegexTemplate.NOT_EMPTY, R.string.oldpassr);
                validation.addValidation(getActivity(), R.id.input_password,  RegexTemplate.NOT_EMPTY, R.string.passerr);
                validation.addValidation(getActivity(), R.id.input_cpass, R.id.input_password, R.string.cpasserr);

                if(validation.validate())
                {
                    String indid = indmap_get_id.get(ind);
                    saveChanges(comp, addr, web,indid);
                }


            }
        });



        return view;
    }

    private void saveChanges(final String comp, final String addr, final String web, final String ind)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("loading...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/updatecompanydetails/id/" + EMP_ID;

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
                        Intent i = new Intent(getContext(), Signin.class);
                        startActivity(i);
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
                params.put("name", comp);
                params.put("website", web);
                params.put("location", addr);
                params.put("industry_id", ind);
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

        String URL_FOR_DETAIL = "http://pdev.work/cvvidapi/api/fetchcompanydetails/id/" + EMP_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Responsejsbjb: " + response.toString());
               // System.out.println("LIST=="+ industryarray.ge);
                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    input_comp.setText(c.getString("name"));
                    input_web.setText(c.getString("website"));
                    input_addr.setText(c.getString("location"));
                    if(!c.getString("industry_id").equals("")) {
                        String indid = c.getString("industry_id");

//                        System.out.println("index==" + indmap_get_index.get("3"));
                        if(indmap_get_index.get(indid) != null) {
                            industryid = Integer.parseInt(indmap_get_index.get(indid))+1;

                            input_ind.setSelection(industryid);
                        }
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

    public void getIndustry() {

        String url = LOCATION_URL.INDUSTRY;

        getResponse(Request.Method.GET, url, null,
                new ESecurityFragment.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        JSONObject j = null;
                        try {
                            industryList = response;
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");

                            //  System.exit(0);
                            for(int i=0;i<result.length();i++){
                                try {
                                    //Getting json object
                                    JSONObject json = result.getJSONObject(i);

                                    //Adding the name of the student to array list
                                    String id =  json.getString(LOCATION_URL.TAG_id);
                                    indmap_get_index.put(id,String.valueOf(i));
                                    indmap_get_id.put(String.valueOf(i),id);
                                    industryarray.add(json.getString(LOCATION_URL.TAG_NAME));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            industryarray.add(0, "Select Industry");
                            //Setting adapter to show the items in the spinner
                         //   input_ind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_spinner_item, industryarray);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            input_ind.setAdapter(adapter);
                           // listIndustry(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }


    public void getResponse(int method, String url, JSONObject jsonValue, final ESecurityFragment.VolleyCallback callback) {

        String cancel_req_tag = LOCATION_URL.INDUSTRY;

        RequestQueue queue = AppSingleton.getInstance(getContext()).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(getContext()).addToRequestQueue(strreq, cancel_req_tag);
    }

    private void listIndustry(JSONArray j){
        //Traversing through all the items in the json array

        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                String id =  json.getString(LOCATION_URL.TAG_id);
                indmap_get_index.put(id,String.valueOf(i));
                indmap_get_id.put(String.valueOf(i),id);
                industryarray.add(json.getString(LOCATION_URL.TAG_NAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        industryarray.add(0, "Select Industry");
        //Setting adapter to show the items in the spinner
        input_ind.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, industryarray));
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
