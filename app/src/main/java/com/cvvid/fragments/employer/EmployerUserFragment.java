package com.cvvid.fragments.employer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.cvvid.activities.candidate.AddHobbies;
import com.cvvid.activities.employer.AddEmployerUser;
import com.cvvid.adaptors.candidate.HobbiesAdaptor;
import com.cvvid.adaptors.employer.EmployerUserAdaptor;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.HobbiesModel;
import com.cvvid.models.employer.EmployerUserModel;
import com.daimajia.swipe.util.Attributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerUserFragment extends Fragment {


    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<EmployerUserModel> mDataSet;

    // Session Manager Class
    SessionManager session;

    private String USER_ID,EMP_ID;
    private String USER_TYPE;
    private String USER_NAME;

    public EmployerUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employer_users, container, false);
        tvEmptyTextView = (TextView)view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataSet = new ArrayList<>();

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
        // emp_id
        EMP_ID = user.get(SessionManager.EMPLOYER_ID);
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        loadData();
        Button adduser = (Button)view.findViewById(R.id.btn_create);

        //        button action
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,AddEmployerUser.class);
                startActivity(intent);
            }
        });

        return view;
    }


    public void loadData() {

        //Creating a string request
         String url = CONFIG_URL+"/getalluser/id/"+EMP_ID;
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");
                    EmployerUserModel item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new EmployerUserModel();
                        String name = c.getString("forenames")+" "+ c.getString("surname");
                        item.setId(c.getString("id"));
                        item.setName(name);
                        item.setEmail(c.getString("email"));
                        item.setPostedjobs(c.getString("postedjobs"));
                        mDataSet.add(new EmployerUserModel(c.getString("id"), name,c.getString("email"),  c.getString("postedjobs")));
                    }

                    if(mDataSet.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        tvEmptyTextView.setVisibility(View.VISIBLE);
                    }else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyTextView.setVisibility(View.GONE);
                    }

                    EmployerUserAdaptor mAdapter = new EmployerUserAdaptor(getContext(), mDataSet);

                    ((EmployerUserAdaptor) mAdapter).setMode(Attributes.Mode.Single);

                    mRecyclerView.setAdapter(mAdapter);

                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            Log.e("RecyclerView", "onScrollStateChanged");
                        }
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });

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
