package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.adaptors.employer.CustomExpandableApplicationListAdapter;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.cvvid.models.employer.ApplicationChildItem;
import com.cvvid.models.employer.ApplicationGroupItemsInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class ViewApplications extends AppCompatActivity {

    ExpandableListView expandableListView;
    PopupWindow popupWindow;
    CustomExpandableApplicationListAdapter adapter;

    private static final String TAG = "Status";

    private String jobid;

    private AlertDialog dialog;

    ProgressDialog progressDialog;
    SessionManager session;

    EditText message;

    private LinkedHashMap<String, ApplicationGroupItemsInfo> songsList = new LinkedHashMap<String, ApplicationGroupItemsInfo>();
    private ArrayList<ApplicationGroupItemsInfo> deptList = new ArrayList<ApplicationGroupItemsInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applications);

        Bundle extras = getIntent().getExtras();
        jobid = extras.getString("jobid");

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        fetchResponse();

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        adapter = new CustomExpandableApplicationListAdapter(getApplicationContext(),deptList);
        expandableListView.setAdapter(adapter);

        //fetchResponse();


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
              // ApplicationParentListItem item =  ListParent.get(groupPosition);
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                ApplicationGroupItemsInfo headerInfo = deptList.get(groupPosition);
                //get the child info
                ApplicationChildItem detailInfo = headerInfo.getList().get(childPosition);

                openPopup(headerInfo.getId());

//                Toast.makeText(getBaseContext(), " List And Song Is :: " + headerInfo.getId()
//                        + "/" + detailInfo.getEmail(), Toast.LENGTH_LONG).show();

                return false;
            }
        });

        TextView applicationcount = (TextView)findViewById(R.id.applicationcount);

        applicationcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ViewApplications.this, ViewApplications.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            }
        });

        TextView shortcount = (TextView)findViewById(R.id.shortcount);

        shortcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ViewApplications.this, ShortlistApplications.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            }
        });

        TextView intercount = (TextView)findViewById(R.id.intercount);

        intercount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ViewApplications.this, IntervieweesApplications.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            }
        });

        TextView questions = (TextView)findViewById(R.id.questions);

        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewApplications.this, EQuestionsList.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            }
        });

        TextView answers = (TextView)findViewById(R.id.answers);

        answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewApplications.this, EAnswersList.class);
                i.putExtra("jobid", jobid);
                startActivity(i);
            }
        });


    }

    private void openPopup(final String app_id)
    {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ViewApplications.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_popup, null);

        MaterialSpinner status = (MaterialSpinner)mView.findViewById(R.id.status);

        String[] arraySpinner = new String[] {
                "New Application", "Shortlist", "Reject"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // Get Selected Class name from the list
                String selectedClass = parent.getItemAtPosition(position).toString();
                switch (selectedClass)
                {
                    case "New Application":

                        break;

                    case "Shortlist":
                        saveStatus("1", app_id);
                        //Toast.makeText(ViewApplications.this, "\n Class: \t " + selectedClass + "\n Div: \t" + selectedClass, Toast.LENGTH_LONG).show();
                        break;

                    case "Reject":
                        openRejectPopup(app_id);
                       // Toast.makeText(ViewApplications.this, "\n Class: \t " + selectedClass + "\n Div: \t" + selectedClass, Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });


        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

    }

    private void openRejectPopup(final String app_id)
    {
        dialog.dismiss();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ViewApplications.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_popup_accept, null);

        message = (EditText)mView.findViewById(R.id.message);
        Button btn_accept = (Button)mView.findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReject(app_id);
            }
        });

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void updateReject(final String id){

        String cancel_req_tag = "Cancel";
        progressDialog.setMessage("loading..");
        showDialog();

        //Creating a string request

        String url = CONFIG_URL+"/rejectapplication/id/"+id;

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
                        Intent i = new Intent(ViewApplications.this, ViewApplications.class);
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("msg", message.getText().toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }


    private void saveStatus(final String status, final String id){

        String cancel_req_tag = "Cancel";
        progressDialog.setMessage("loading..");
        showDialog();

        //Creating a string request

        String url = CONFIG_URL+"/updateapplicationstatus/id/"+id;

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
                        Intent i = new Intent(ViewApplications.this, ViewApplications.class);
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", status);
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void fetchResponse()
    {
        String url = CONFIG_URL+"/getalljobapplications/id/"+jobid;
        //Creating a string request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        addProduct(c.getString("id"), c.getString("forenames")+" "+c.getString("surname"),c.getString("email"),c.getString("created_at"),
                                c.getString("status"));
                    }

                    adapter = new CustomExpandableApplicationListAdapter(getApplicationContext(),deptList);
                    expandableListView.setAdapter(adapter);
                 //   adapter.notifyDataSetChanged();


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

        String counturl = CONFIG_URL+"/getalljobcount/id/"+jobid;

//        getcount
        final StringRequest stringRequest1 = new StringRequest(Request.Method.GET,
                counturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        TextView applicationcount = (TextView)findViewById(R.id.applicationcount);
                        applicationcount.setText("Applications ("+c.getString("appcount")+ ")");

                        TextView shortcount = (TextView)findViewById(R.id.shortcount);
                        shortcount.setText("Shortlisted ("+c.getString("shortcount")+ ")");

                        TextView intercount = (TextView)findViewById(R.id.intercount);
                        intercount.setText("Interviewees ("+c.getString("intcount")+ ")");

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
        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        //Adding request to the queue
        requestQueue1.add(stringRequest1);
    }

    // here we maintain songsList and songs names
    private int addProduct(String id,String name,String email, String status, String date) {

        int groupPosition = 0;

        //check the hashmap if the group already exists
        ApplicationGroupItemsInfo headerInfo = songsList.get(id);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new ApplicationGroupItemsInfo();
            headerInfo.setId(id);
            headerInfo.setName(name);

            songsList.put(name, headerInfo);

            deptList.add(headerInfo);
        }

        // get the children for the group
        ArrayList<ApplicationChildItem> productList = headerInfo.getList();
        // size of the children list
        int listSize = productList.size();
        // add to the counter
        listSize++;

        // create a new child and add that to the group
        ApplicationChildItem detailInfo = new ApplicationChildItem();
        detailInfo.setEmail(email);
        detailInfo.setDate(date);
        detailInfo.setStatus(status);
        productList.add(detailInfo);
        headerInfo.setList(productList);

        // find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
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
