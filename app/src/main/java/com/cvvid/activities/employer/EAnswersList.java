package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.CExperienceAdaptor;
import com.cvvid.adaptors.employer.EAnswersAdaptor;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CExperienceFragment;
import com.cvvid.models.candidate.CEItemObject;
import com.cvvid.models.employer.AnswersModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EAnswersList extends AppCompatActivity {

    ListView listView;
    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String PROFILE_ID;

    private static final String TAG = EAnswersList.class.getSimpleName();
    private RecyclerView recyclerView;


    private String jobid;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eanswers_list);

        TextView btn_back = (TextView)findViewById(R.id.btn_back);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        jobid = extras.getString("jobid");


//        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        PROFILE_ID = user.get(SessionManager.PROFILE_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        recyclerView = (RecyclerView)findViewById(R.id.add_header);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        getDataSource();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getDataSource(){

        final List<AnswersModel> data = new ArrayList<AnswersModel>();

        String url = CONFIG_URL+"/getallanswers/id/"+jobid;
        //Creating a string request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");
                    CEItemObject item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("forenames")+" "+c.getString("surname");
                        String question = c.getString("question");
                        String video = c.getString("video");

                        data.add(new AnswersModel(id,name, question,video));

                        EAnswersAdaptor customAdapter = new EAnswersAdaptor(getApplicationContext(),data, jobid);
                        recyclerView.setAdapter(customAdapter);
                        // return data;

                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
