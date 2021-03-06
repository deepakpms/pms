package com.cvvid.fragments.institute;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.activities.employer.EditCompanyBackground;
import com.cvvid.activities.institution.EditSchoolBackground;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_GET;
import static com.cvvid.apicall.LOCATION_URL.SCHOOL_BACKGROUND_GET;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolBackgroundFragment extends Fragment {

    private TextView cb_field;

    // Session Manager Class
    SessionManager session;

    private String USER_ID,INSTITUTION_ID;
    private String USER_TYPE;
    private String USER_NAME;


    public SchoolBackgroundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_school_background, container, false);

        cb_field = (TextView)view.findViewById(R.id.cb_text);
        Button edit_cb = (Button)view.findViewById(R.id.btn_edit);

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

        // user_id
        INSTITUTION_ID = user.get(SessionManager.INSTITUTION_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        loadData(USER_ID);



        //        button action
        edit_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,EditSchoolBackground.class);
                startActivity(intent);
            }
        });


        return view;
    }

    public void loadData(String USER_ID) {

        String url = SCHOOL_BACKGROUND_GET+INSTITUTION_ID;
        //Creating a string request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");
                    JSONObject c = jUser.getJSONObject(0);

                    String background = c.getString("body");
                    cb_field.setText(Html.fromHtml(background));
//                    cb_field.setText(background);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

}
