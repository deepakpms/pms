package com.cvvid.fragments.employer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.activities.candidate.AddDocument;
import com.cvvid.activities.employer.EditCompanyBackground;
import com.cvvid.adaptors.candidate.DocumentAdaptor;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.DocumentModel;
import com.daimajia.swipe.util.Attributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_EDIT;
import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_BACKGROUND_GET;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyBackgroundFragment extends Fragment {

    private TextView cb_field;

    // Session Manager Class
    SessionManager session;

    private String USER_ID,EMP_ID;
    private String USER_TYPE;
    private String USER_NAME;


    public CompanyBackgroundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_company_background, container, false);

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
        EMP_ID = user.get(SessionManager.EMPLOYER_ID);

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
                Intent intent = new Intent(getActivity() ,EditCompanyBackground.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void loadData(String USER_ID) {

        String url = EMPLOYER_BACKGROUND_GET+EMP_ID;
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
