package com.cvvid.fragments.employer;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateFavourites;
import com.cvvid.activities.candidate.ProfilesViews;
import com.cvvid.activities.employer.EmployerFavourites;
import com.cvvid.activities.employer.EmployerVideoLikes;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicDetailsFragment extends Fragment {

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;

    ProgressDialog progressDialog;

    public BasicDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emp_basic_details, container, false);

        //TextView num_likes = (TextView)view.findViewById(R.id.num_likes);
       // TextView favourites = (TextView)view.findViewById(R.id.favourites);
        //TextView num_fav = (TextView)view.findViewById(R.id.num_likes);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

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

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        getLoadData(USER_ID, view);


        //        button action
//        num_likes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity() ,EmployerVideoLikes.class);
//                startActivity(intent);
//            }
//        });
//        //        button action
//        favourites.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity() ,EmployerFavourites.class);
//                startActivity(intent);
//            }
//        });


        return view;
    }

    private void getLoadData(final String USER_ID, final View view)
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = LOCATION_URL.INSTITUTE_DETAIL + USER_ID;

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

                    String stripe_active = c.getString("stripe_active");
                    String slug = c.getString("slug");
                    String acctype="";
                    if(stripe_active.equals("1")){
                        acctype = "Premium Account";
                    }else{
                        acctype = "Basic Account";
                    }

                    TextView compname = (TextView) view.findViewById(R.id.compname);
                    compname.setText(c.getString("name"));
                    TextView accounttype = (TextView) view.findViewById(R.id.accounttype);
                    accounttype.setText(acctype);
//                    TextView website = (TextView) view.findViewById(R.id.website);
//                    website.setText(c.getString("website"));
                    TextView location = (TextView) view.findViewById(R.id.location);
                    if(!c.getString("location").equals(""))
                        location.setText(c.getString("location"));
                    else
                        location.setText("No data");
//                    TextView viewtxt = (TextView) view.findViewById(R.id.num_views);
//                    viewtxt.setText(c.getString("num_views")+" Views");
//                    TextView favtxt = (TextView) view.findViewById(R.id.num_likes);
//                    favtxt.setText(c.getString("num_likes")+" Likes");

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
