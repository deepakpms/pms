package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.adaptors.employer.EQuestionsAdaptor;
import com.cvvid.adaptors.employer.OfficeAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.cvvid.common.SimpleDividerItemDecoration;
import com.cvvid.models.employer.EmployerOfficeModel;
import com.cvvid.models.employer.QuestionsModel;
import com.daimajia.swipe.util.Attributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EQuestionsList extends AppCompatActivity {

    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<QuestionsModel> mDataSet;

    private AlertDialog dialog;

    ProgressDialog progressDialog;

    private static final String TAG = "Status";

    EditText questions;

    SessionManager session;

    private String jobid;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String EMPLOYER_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equestions_list);

        tvEmptyTextView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        Button btn_add_ques = (Button)findViewById(R.id.btn_add_ques);
        TextView btn_back = (TextView)findViewById(R.id.btn_back);

        //        session
        session = new SessionManager(EQuestionsList.this);

        Bundle extras = getIntent().getExtras();
        jobid = extras.getString("jobid");

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        EMPLOYER_ID = user.get(SessionManager.EMPLOYER_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(EQuestionsList.this));
        mDataSet = new ArrayList<>();
        loadData();

        btn_add_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuestionPopup();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void openQuestionPopup()
    {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EQuestionsList.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_popup_questions, null);

        questions = (EditText)mView.findViewById(R.id.questions);
        Button btn_accept = (Button)mView.findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReject();
            }
        });

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void updateReject(){

        String cancel_req_tag = "Cancel";
        progressDialog.setMessage("loading..");
        showDialog();

        //Creating a string request

        String url = CONFIG_URL+"/addnewquestions/id/"+jobid;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Status Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        //dialog.dismiss();
                        Intent i = new Intent(EQuestionsList.this, EQuestionsList.class);
                        i.putExtra("jobid", jobid);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Questions Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("question", questions.getText().toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }



    public void loadData() {

        String url = CONFIG_URL+"/getalljobquestions/id/"+jobid;
        //Creating a string request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");
                    QuestionsModel item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new QuestionsModel();

                        item.setId(c.getString("id"));
                        item.setQuestions(c.getString("question"));

                        mDataSet.add(new QuestionsModel(c.getString("id"), c.getString("question")));
                    }

                    if(mDataSet.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        tvEmptyTextView.setVisibility(View.VISIBLE);
                    }else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyTextView.setVisibility(View.GONE);
                    }

                    EQuestionsAdaptor mAdapter = new EQuestionsAdaptor(EQuestionsList.this, mDataSet, jobid);

                    ((EQuestionsAdaptor) mAdapter).setMode(Attributes.Mode.Single);

                    mRecyclerView.setAdapter(mAdapter);

                    mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                            getApplicationContext()
                    ));

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
        RequestQueue requestQueue = Volley.newRequestQueue(EQuestionsList.this);

//        Adding request to the queue
        requestQueue.add(stringRequest);

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
