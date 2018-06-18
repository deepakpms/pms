package com.cvvid.fragments.employer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.CandidateMessageUserAdaptor;
import com.cvvid.adaptors.employer.EmployerMessageUserAdaptor;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.CandidateMessageUserList;
import com.cvvid.models.employer.EmployerMessageUserList;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerMessageFragment extends Fragment {

    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<EmployerMessageUserList> mDataSet;

    // Session Manager Class
    SessionManager session;


    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;


    public EmployerMessageFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        EmployerMessageFragment myFragment = new EmployerMessageFragment();
        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate_message, container, false);

        tvEmptyTextView = (TextView)view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

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

        loadMessageUserList(USER_ID);

        return view;

    }

    public static String forReplacementString(String aInput){
        return Matcher.quoteReplacement(aInput);
    }
    public void loadMessageUserList(String USER_ID) {

        String url = CONFIG_URL+"/getconversationmembers/id/"+USER_ID;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                   String res = forReplacementString(response);

                    Gson gson = new Gson();

                    JSONObject jarray = new JSONObject(res);

                    JSONArray jUser = jarray.getJSONArray("data");

                    EmployerMessageUserList item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new EmployerMessageUserList();


                        item.setConversation_id("conversation_id");
                        item.setUser_id(c.getString("user_id"));
                        item.setName(c.getString("name"));
                        item.setCreated_at(c.getString("created_at"));
                        item.setSubject(c.getString("subject"));
                        item.setMessage(c.getString("message"));

                        mDataSet.add(new EmployerMessageUserList(c.getString("conversation_id"), c.getString("user_id"), c.getString("name"), c.getString("subject"),c.getString("message"),c.getString("created_at")));
                    }

                    if(mDataSet.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        tvEmptyTextView.setVisibility(View.VISIBLE);
                    }else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyTextView.setVisibility(View.GONE);
                    }

                    EmployerMessageUserAdaptor mAdapter = new EmployerMessageUserAdaptor(getContext(), mDataSet);

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
