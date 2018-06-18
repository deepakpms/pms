package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.cvvid.adaptors.candidate.SearchJobAdaptor;
import com.cvvid.adaptors.employer.SearchCandidateAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.common.ViewPagerAdapter;
import com.cvvid.fragments.employer.CandidateSearchFragment;
import com.cvvid.fragments.employer.EmployerProfileFragment;
import com.cvvid.models.candidate.SearchItemJobList;
import com.cvvid.models.employer.SearchItemCandidateList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class FilterCandidateList extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter madapter;
    private ImageView logout;
    private Context c;
    private AutoCompleteTextView mAutocompleteTextView;
    private String USER_ID;
    private String USER_NAME;
    private String USER_TYPE;

    private TextView forgotpass;
    // Session Manager Class
    SessionManager session;
    private AutoCompleteTextView location;
    ProgressDialog progressDialog;
    final ArrayList<FilterCandidateList.SkillsClass> skillslist = new ArrayList<FilterCandidateList.SkillsClass>();
    MaterialSpinner scategory,distance;
    TextView skillstext;
    public static String categoryList="";
    public static String skillsList="";
    private ArrayList<String> categoryarray;
    String skill_txt,radius;
    public static String category_id = "";
    public static String state_id = "";
    public String[] skills;
    public boolean[] checkedskills;
    private JSONArray result;
    private ArrayList<String> distancearray;
    private Button searchjob;
    HashMap<String ,String> catmap;
    private Button searchcandidate;
    private TextView cname;
    private String locationval="";
    private double latitude;
    private double longitude;
    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_candidate);


        // Progress dialog
        progressDialog = new ProgressDialog(FilterCandidateList.this);
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(FilterCandidateList.this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        scategory =(MaterialSpinner) findViewById(R.id.input_category);

        location =(AutoCompleteTextView) findViewById(R.id.input_location);
        searchcandidate = (Button)findViewById(R.id.searchcandidate);
        catmap = new HashMap<String,String>();
        categoryarray = new ArrayList<String>();

        skillstext = (TextView)findViewById(R.id.skills);
        distance = (MaterialSpinner)findViewById(R.id.distance);

        RelativeLayout skillslayout = (RelativeLayout)findViewById(R.id.addskills);

        ImageView close = (ImageView) findViewById(R.id.close_activity);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }

        });
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        //session.checkLogin();

        scategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                skillstext.setText("");
                Integer value = scategory.getSelectedItemPosition();

                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(categoryList);
                    result = jsonResponse.getJSONArray("data");
                    if(value > 0) {
                        JSONObject jsonn = result.getJSONObject(value - 1);
                        // category_id = jsonn.getString("id");
                        category_id = catmap.get(String.valueOf(value));
                        skills();
                        //religionname = val;//
                    } else {
                        // religionname = "";
                    }




                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        skillslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FilterCandidateList.this);
                if(scategory.getSelectedItemPosition() > 0) {
                    int count = skills.length;
                    checkedskills = new boolean[count];

                    // make a list to hold state of every color
                    for (int i = 0; i < skills.length; i++) {
                        FilterCandidateList.SkillsClass EatingClass = new FilterCandidateList.SkillsClass();
                        EatingClass.setName(skills[i]);
                        EatingClass.setSelected(checkedskills[i]);
                        skillslist.add(EatingClass);
                    }


                    // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')
                    builder.setMultiChoiceItems(skills, checkedskills, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            // set state to vo in list

//                        if (which == 0 && isChecked == true) {
//
//                            for (int i = 1; i < checkedskills.length; i++) {
                            //checkedskills[i] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                            skillslist.get(which).setSelected(isChecked);
                            //  skillslist.get(i).setSelected(false);

//                            }


//                        }else{
//                            checkedskills[0] = false;
//                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
//                            skillslist.get(which).setSelected(isChecked);
//                            skillslist.get(0).setSelected(false);
//                        }
                        }
                    });

                    builder.setCancelable(false);

                    builder.setTitle("Select Career Paths");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            skillstext.setText("");

                            // save state of selected vos
                            ArrayList<FilterCandidateList.SkillsClass> selectedList = new ArrayList<>();
                            // System.out.println("select"+skillslist.size());
                            for (int i = 0; i < skillslist.size(); i++) {
                                FilterCandidateList.SkillsClass EatingClass = skillslist.get(i);
                                skills[i] = EatingClass.getName();
                                checkedskills[i] = EatingClass.isSelected();


                                if (EatingClass.isSelected()) {
                                    selectedList.add(EatingClass);
                                }
                            }


                            for (int i = 0; i < selectedList.size(); i++) {
                                // if element is last then not attach comma or attach it


                                if (i != selectedList.size() - 1)
                                    skillstext.setText(skillstext.getText() + selectedList.get(i).getName() + ",");
                                else
                                    skillstext.setText(skillstext.getText() + selectedList.get(i).getName());
                            }
                            skillslist.clear();
                        }
                    });


                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // make sure to clear list that duplication dont formed here
                            skillslist.clear();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    Toast.makeText(FilterCandidateList.this,
                            "Please select skill category",
                            Toast.LENGTH_LONG).show();
                }
            }
            //  }

        });

        searchcandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchcandidate();

            }
        });

        getCategory();


    }

    private void searchcandidate()
    {

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();
        lat = Double.toString(latitude).equals("0.0") ? "0" : Double.toString(latitude);
        lng = Double.toString(longitude).equals("0.0") ? "0" : Double.toString(longitude);

        skill_txt = skillstext.getText().toString();
        radius = "";

        if(distance.getSelectedItem() == "Within 5 miles")
            radius = "5";
        else if(distance.getSelectedItem() == "Within 10 miles")
            radius = "10";
        else if(distance.getSelectedItem() == "Within 20 miles")
            radius = "20";
        else if(distance.getSelectedItem() == "Within 30 miles")
            radius = "30";
        else if(distance.getSelectedItem() == "Within 40 miles")
            radius = "40";
        else if(distance.getSelectedItem() == "Within 50 miles")
            radius = "50";

        String URL_FOR_QSEARCH = CONFIG_URL+"/searchcandidates?skill_category="+category_id+"&skills="+skill_txt+"&location="+locationval+"&latitude="+lat+"&longitude="+lng+"&radius="+radius;

        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        String urlEncoded = Uri.encode(URL_FOR_QSEARCH, ALLOWED_URI_CHARS);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                urlEncoded, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String msg =  jsonObj.getString("message");

                    if (msg.equals("Not Found"))
                    {
                        Toast.makeText(FilterCandidateList.this,"No Candidates matched for the selected criteria.",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Intent intent = new Intent(FilterCandidateList.this,SearchCandidateList.class);
                        intent.putExtra("identity","candidate");
                        intent.putExtra("skill_category",category_id);
                        intent.putExtra("skills",skill_txt);
                        intent.putExtra("location",locationval);
                        intent.putExtra("longitude",lat);
                        intent.putExtra("latitude",lng);
                        intent.putExtra("radius",radius);
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(FilterCandidateList.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) ;

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Adding request to request queue
        AppSingleton.getInstance(FilterCandidateList.this).addToRequestQueue(strReq,cancel_req_tag);



    }

    public static String[] toStringArray(JSONArray array) throws JSONException {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        List<String> list = new ArrayList<String>();
        for(int i=0; i<arr.length; i++) {
            list.add(array.getJSONObject(i).getString("name"));
        }
        String[] stringArray = list.toArray(new String[list.size()]);
        // System.out.println("sssss"+stringArray.toString());
        return stringArray;
    }

    private String[] skills()
    {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(LOCATION_URL.SKILLS + category_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            skillsList = response;
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");
                            skills = toStringArray(result);
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
        RequestQueue requestQueue = Volley.newRequestQueue(FilterCandidateList.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

        return null;
    }

    private void getCategory() {
        //Creating a string request
        skillslist.clear();
        StringRequest stringRequest = new StringRequest(LOCATION_URL.CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            categoryList = response;

                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");
                            String categoryid = "";
                            catmap.put("Select Skill Category","0");
                            categoryarray.add("Select Skill Category");
                            for (int i = 1; i < (result.length()+1); i++) {
                                try {

                                    //Getting json object
                                    JSONObject json = result.getJSONObject(i);
                                    //Adding the name of the student to array list
                                    categoryid = json.getString("id");
                                    catmap.put(String.valueOf(i),categoryid);
                                    categoryarray.add(json.getString("name"));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //Setting adapter to show the items in the spinner
                            scategory.setAdapter(new ArrayAdapter<String>(FilterCandidateList.this, R.layout.material_spinner, categoryarray));


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
        RequestQueue requestQueue = Volley.newRequestQueue(FilterCandidateList.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    private class SkillsClass {

        private String name;
        private boolean selected;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }


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
