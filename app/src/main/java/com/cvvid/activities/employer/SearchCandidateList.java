package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.SearchJobAdaptor;
import com.cvvid.adaptors.employer.SearchCandidateAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.fragments.employer.EmployerProfileFragment;
import com.cvvid.models.candidate.SearchItemJobList;
import com.cvvid.models.employer.SearchItemCandidateList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class SearchCandidateList extends AppCompatActivity {

    private static final String TAG = "SEARCHRESULTS";
    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<SearchItemCandidateList> mDataSet;
    ProgressDialog progressDialog;
    TextView cname;
    private static String QuickSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_candidate_list);


        tvEmptyTextView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchCandidateList.this));
        mDataSet = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final String IDENTITY = getIntent().getExtras().getString("identity");

        if(IDENTITY.equals("candidate")){

            final String skill_category = getIntent().getExtras().getString("skill_category");
            final String skills = getIntent().getExtras().getString("skills");
            final String location = getIntent().getExtras().getString("location");
            final String longitude = getIntent().getExtras().getString("longitude");
            final String latitude = getIntent().getExtras().getString("latitude");

            String URL_FOR_QSEARCH = CONFIG_URL+"/searchcandidates?skill_category="+skill_category+"&skills="+skills+"location="+location
                                    +"longitude="+longitude+"latitude="+latitude;

            String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
            QuickSearch = Uri.encode(URL_FOR_QSEARCH, ALLOWED_URI_CHARS);

        }

        loadRecyclerdata();

        ImageView filter = (ImageView) findViewById(R.id.filter_candidate);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchCandidateList.this, FilterCandidateList.class);
                startActivity(i);
            }

        });

        ImageView close = (ImageView) findViewById(R.id.close_activity);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onBackPressed();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadRecyclerdata(){

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("loading..");

        showDialog();

        System.out.println("QuickSearch"+QuickSearch);

        final StringRequest strReq = new StringRequest(Request.Method.GET, QuickSearch, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "SearchList Response: " + response.toString());

                try {

                    JSONObject jarray = new JSONObject(response);
                    SearchItemCandidateList item;
                    JSONArray jUser = jarray.getJSONArray("data");

                    System.out.println("user--"+jUser);

                    for (int i = 0; i < jUser.length(); i++) {

                        JSONObject c = jUser.getJSONObject(i);

                        item = new SearchItemCandidateList();
                        item.setId(c.getString("id"));
                        item.setname(c.getString("name"));
                        item.setJob(c.getString("current_job"));
                        item.setLocation(c.getString("location"));
                        item.setImage(c.getString("photo"));

                        mDataSet.add(new SearchItemCandidateList(c.getString("id"), c.getString("name"),c.getString("skills"),c.getString("location"), c.getString("current_job"), c.getString("photo")));
                    }

                    if(mDataSet.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        tvEmptyTextView.setVisibility(View.VISIBLE);
                    }else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyTextView.setVisibility(View.GONE);
                    }

                    SearchCandidateAdaptor mAdapter = new SearchCandidateAdaptor(SearchCandidateList.this, mDataSet);
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

                    hideDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

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
