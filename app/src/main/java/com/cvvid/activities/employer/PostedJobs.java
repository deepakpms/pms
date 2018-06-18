package com.cvvid.activities.employer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.activities.candidate.ImageLibraryCandidate;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CBusinessCard;
import com.cvvid.models.employer.PostedJobsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import fr.ganfra.materialspinner.MaterialSpinner;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class PostedJobs extends AppCompatActivity {

    private static String[] jobsHeader = {"Job Title", "Salary" , "Type", "Location", "Applications"};

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String EMP_ID;

    public ArrayList<String> userslist;

    public String[][] jobdata;
    public static String jobList="";
    private JSONArray result;

    HashMap<String ,String> usermap,userindex;

    private android.support.v7.app.AlertDialog dialog;

    private List<PostedJobsModel> profileViewList;

    private SortableTableView<String[]> tableView;

    // Session Manager Class
    SessionManager session;

    private ArrayAdapter<String> adapter;
    public static String userList="";
    private MaterialSpinner users;
    public static String userid="";

    private Button btn_create;

    ProgressDialog progressDialog;

    private static final String TAG = PostedJobs.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_jobs);

        profileViewList = new ArrayList<>();

        userslist = new ArrayList<String>();

        usermap = new HashMap<String,String>();
        userindex = new HashMap<String,String>();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //        session
        session = new SessionManager(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        EMP_ID = user.get(SessionManager.EMPLOYER_ID);

        skills();

        tableView = (SortableTableView<String[]>) findViewById(R.id.postedjobs);
//        tableView.setColumnCount(4);
//        tableView.setHasFixedWidth(false);

//        tableView.setColumnWidth(0, 1);

//        tableView.setDividerPadding(20);
//        tableView.setColumnWeight(0, 4);
//        tableView.setColumnWeight(1, 4);
//        tableView.setPadding(10,10,10,10);
//        tableView.setColumnWeight(1, 4);
//        tableView.setColumnWeight(2, 4);
//        tableView.setColumnWeight(3, 4);
//        tableView.setColumnWeight(4, 4);
//        tableView.setColumnWeight(2, 2);
//        tableView.setColumnWeight(3, 2);
//        tableView.setHeaderBackgroundColor(Color.YELLOW);
//
        tableView.addDataClickListener(new TableDataClickListener<String[]>() {
            @Override
            public void onDataClicked(int rowIndex, String[] clickedData) {

                selectaction(((String[])clickedData)[4], ((String[])clickedData)[0]);
               // Toast.makeText(getApplicationContext(), ((String[])clickedData)[5], Toast.LENGTH_LONG).show();

            }
        });

        btn_create = (Button) findViewById(R.id.btn_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostedJobs.this ,AddJob.class);
                startActivity(intent);
            }
        });




    }

    public void selectaction(final String id, String name)
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(name);
//        alertDialog.SetCancelable (false);
        alertDialog.setIcon(R.drawable.ic_card_travel_black_24dp);

// add a list
        String[] animals = {"Edit Job","View Applicants", "Assign", "Duplicate"};
        alertDialog.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent i = new Intent(getApplicationContext(), EditJobs.class);
                        i.putExtra("jobid", id);
                        startActivity(i);
                        break;
                    case 1:
                        Intent ie = new Intent(getApplicationContext(), ViewApplications.class);
                        ie.putExtra("jobid", id);
                        startActivity(ie);
                        break;
                    case 2:
                        openAssignPopup(id);
                        break;
                    case 3:
                        Intent de = new Intent(getApplicationContext(), ViewApplications.class);
                        de.putExtra("jobid", id);
                        startActivity(de);
                        break;
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = alertDialog.create();
        dialog.show();


    }

    private void openAssignPopup(final String jobid)
    {
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(PostedJobs.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_popup_assign, null);

        users = (MaterialSpinner)mView.findViewById(R.id.users);
        Button btn_assign = (Button)mView.findViewById(R.id.btn_assign);

        getUsers();

        users.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                MaterialSpinner spinnerv = (MaterialSpinner) parent;

                Integer value = users.getSelectedItemPosition();

                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(userList);
                    result = jsonResponse.getJSONArray("data");
                    if (value > 0) {
                        JSONObject jsonn = result.getJSONObject(value - 1);
                        String val = jsonn.getString("id");
                        userid = val;
                    } else {
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        btn_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignJob(jobid);
            }
        });


        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void assignJob(final String id){

        String cancel_req_tag = "Cancel";
        progressDialog.setMessage("loading..");
        showDialog();

        //Creating a string request

        String url = CONFIG_URL+"/assignjob/id/"+id;

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
                        Intent i = new Intent(PostedJobs.this, PostedJobs.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Job assigned successfully!", Toast.LENGTH_LONG).show();
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
                params.put("userid", userid);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void getUsers(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(LOCATION_URL.USER_ALL+USER_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            userList = response;
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");
                            //userslist = toStringArray(result);

                            for(int i=0;i<result.length();i++){
                                try {
                                    //Getting json object
                                    JSONObject json = result.getJSONObject(i);

                                    String id =  json.getString(LOCATION_URL.TAG_id);
                                    usermap.put(String.valueOf(i),id);
                                    userindex.put(id,String.valueOf(i));

                                    userslist.add(json.getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            userslist.add(0, "- Select User -");

                            adapter = new ArrayAdapter<String>(PostedJobs.this,
                                    android.R.layout.simple_spinner_item, userslist);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            users.setAdapter(adapter);


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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void skills()
    {

        String url = CONFIG_URL+"/getallpostedjobs/id/"+EMP_ID;

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");

                            PostedJobsModel item;

                            for (int i = 0; i < result.length(); i++)
                            {
                                JSONObject c = result.getJSONObject(i);

                                item = new PostedJobsModel();

                                item.setId(c.getString("id"));
                                item.setJob_title(c.getString("title"));
                                String sal = "£"+c.getString("salary_min")+"-"+"£"+c.getString("salary_max");
                                item.setSalary(sal);
                                item.setLocation(c.getString("location"));
                                item.setType(c.getString("type"));

                                profileViewList.add(item);
                            }

                            jobdata = new String[profileViewList.size()][5];

                            for (int i=0; i<profileViewList.size();i++)
                            {
                                PostedJobsModel s = profileViewList.get(i);

                                jobdata[i][0] = s.getJob_title();
                                jobdata[i][1] = s.getSalary();
                                jobdata[i][2] = s.getType();
                                jobdata[i][3] = s.getLocation();
                                jobdata[i][4] = s.getId();
                            }


                            tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(PostedJobs.this, jobsHeader));
                            tableView.setDataAdapter(new SimpleTableDataAdapter(PostedJobs.this, jobdata));

                            //skills = toStringArray(result);
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
