package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.activities.candidate.EditCExprience;
import com.cvvid.activities.candidate.EditHobbies;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.fragments.candidate.CBusinessCard;
import com.cvvid.fragments.candidate.JobSearchFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.URL_FETCH_JOB;
import static com.cvvid.apicall.LOCATION_URL.URL_UPDATE_JOB;

public class EditJobs extends AppCompatActivity {

    private JSONArray result;
    private ArrayList<String> industryarray;
    MaterialSpinner sindustry;
    public static String industryList="";
    public static String skillsList="";
    public static String industry_id="";
    private static final String TAG = "EditJobs";
    ProgressDialog progressDialog;
    MaterialSpinner scategory,reg_category_field;
    TextView skillstext;
    public static String categoryList="";
    private ArrayList<String> categoryarray;
    HashMap<String ,String> catmap,catindex;
    public String[] skills;
    public boolean[] checkedskills;
    final ArrayList<EditJobs.SkillsClass> skillslist = new ArrayList<EditJobs.SkillsClass>();
    EditText title_field,loc_field,end_date_field,salary_min_field,salary_max_field,description_field,additional_field;
    RadioButton annual_field,hourly_field;
    public static String category_id = "";
    private String salarytype = "annual";
    private HashMap<String,String> imap,imap_getid;
    private Button btn_update;
    String jobid;
    String jtype = "";
    String sal_type = "";
    public String[] subskills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jobs);

        industryarray = new ArrayList<String>();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getIndustry();

        // decalaration of variables
        sindustry = (MaterialSpinner)findViewById(R.id.industry_id);
        scategory =(MaterialSpinner) findViewById(R.id.input_category);
        title_field = (EditText) findViewById(R.id.title);
        reg_category_field = (MaterialSpinner)findViewById(R.id.reg_category);
        loc_field = (EditText) findViewById(R.id.location);
        end_date_field = (EditText) findViewById(R.id.end_date);
        salary_min_field = (EditText) findViewById(R.id.salary_min);
        salary_max_field = (EditText) findViewById(R.id.salary_max);
       // input_category_field = (MaterialSpinner)findViewById(R.id.input_category);
        description_field = (EditText)findViewById(R.id.description);
        additional_field = (EditText)findViewById(R.id.additional);
        annual_field = (RadioButton)findViewById(R.id.annual);
        hourly_field = (RadioButton)findViewById(R.id.hourly);
        imap = new HashMap<String, String>();
        imap_getid = new HashMap<String, String>();

        skillstext = (TextView) findViewById(R.id.skills);
        RelativeLayout skillslayout = (RelativeLayout)findViewById(R.id.addskills);
        categoryarray = new ArrayList<String>();
        catmap = new HashMap<String,String>();
        catindex = new HashMap<String,String>();
        btn_update = (Button) findViewById(R.id.btn_update);
        getCategory();

        Intent i = getIntent();
        jobid = i.getStringExtra("jobid");


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               saveJob();
            }
        });

        sindustry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position != -1) {

                    Integer value = sindustry.getSelectedItemPosition();

                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(industryList);
                        result = jsonResponse.getJSONArray("data");
                        if (value > 0) {
                            JSONObject jsonn = result.getJSONObject(value - 1);
                            String val = jsonn.getString("id");
                            industry_id = val;
                        } else {
                            industry_id = "";
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        scategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
              //  skillstext.setText("");
                Integer value = scategory.getSelectedItemPosition();

                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(categoryList);
                    result = jsonResponse.getJSONArray("data");
                    if(value > 0) {
                        //JSONObject jsonn = result.getJSONObject(value-1);
                        category_id = catmap.get(String.valueOf(value));//jsonn.getString("id");
                        System.out.println("CAT=="+category_id);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditJobs.this);
                if(!category_id.isEmpty()) {
                    int count = skills.length;
                    checkedskills = new boolean[count];

                    String[] bob = subskills;

                    // make a list to hold state of every color
                    for (int i = 0; i < skills.length; i++) {

                        if (Arrays.asList(bob).contains(skills[i]))
                            checkedskills[i]=true;

                        EditJobs.SkillsClass EatingClass = new EditJobs.SkillsClass();
                        EatingClass.setName(skills[i]);
                        EatingClass.setSelected(checkedskills[i]);
                        skillslist.add(EatingClass);
                    }


                    // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')
                    builder.setMultiChoiceItems(skills, checkedskills, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            // set state to vo in list

//                            if (which == 0 && isChecked == true) {
//
//                                for (int i = 1; i < checkedskills.length; i++) {
                            //checkedskills[i] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                            skillslist.get(which).setSelected(isChecked);
//                                    skillslist.get(i).setSelected(false);
//
//                                }
//
//
//                            } else {
                            //checkedskills[0] = false;
//                                ((AlertDialog) dialog).getListView().setItemChecked(0, false);
//                                skillslist.get(which).setSelected(isChecked);
//                                skillslist.get(0).setSelected(false);
//                            }
                        }
                    });

                    builder.setCancelable(false);

                    builder.setTitle("Select Career Paths");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           skillstext.setText("");

                            // save state of selected vos
                            ArrayList<EditJobs.SkillsClass> selectedList = new ArrayList<>();
                            // System.out.println("select"+skillslist.size());
                            for (int i = 0; i < skillslist.size(); i++) {
                                EditJobs.SkillsClass EatingClass = skillslist.get(i);
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
                    Toast.makeText(getApplicationContext(),
                            "Please select skill category",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        setValues(jobid);


    }

    private void setValues(String jobsid)
    {
        String cancel_req_tag = "edit";

        String URL_FOR_DETAIL = URL_FETCH_JOB + jobsid;

        getResponse(Request.Method.GET, URL_FOR_DETAIL, null,
                new EditJobs.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        Log.d(TAG, "MyProfile Response: " + response.toString());

                        hideDialog();
                        try {

                            JSONObject jarray = new JSONObject(response);

                            JSONArray jUser = jarray.getJSONArray("data");

                            JSONObject c = jUser.getJSONObject(0);

                            String title = c.getString("title");
                            String desc = c.getString("description");
                            String loc = c.getString("location");
                            String end = c.getString("end_date");
                            String salary_min = c.getString("salary_min");
                            String salary_max = c.getString("salary_max");
                            String additional = c.getString("additional");
                            String indid = c.getString("industry_id");
                            String skillcatid = c.getString("skill_category_id");

                            title_field.setText(title);
                            loc_field.setText(loc);

                            String indpos = imap.get(indid);
                            if(indpos != null)
                                sindustry.setSelection(Integer.parseInt(indpos) + 1);

                            end_date_field.setText(end);
                            salary_min_field.setText(salary_min);
                            salary_max_field.setText(salary_max);

                            String skillpos = catindex.get(skillcatid);
                            if(skillpos != null)
                            scategory.setSelection(Integer.parseInt(skillpos));

                            //  input_category_field.setSelection(1);

                            description_field.setText(desc);
                            additional_field.setText(additional);

                            if(c.getString("type").equalsIgnoreCase("Permanent"))
                                reg_category_field.setSelection(0);
                            else if(c.getString("type").equalsIgnoreCase("Temporary"))
                                reg_category_field.setSelection(1);
                            else if(c.getString("type").equalsIgnoreCase("Fixed Term Contract"))
                                reg_category_field.setSelection(2);


                            if(c.getString("salary_type").equalsIgnoreCase("annual")) {
                                annual_field.setChecked(true);
                                hourly_field.setChecked(false);
                            }
                            if(c.getString("salary_type").equalsIgnoreCase("hourly")) {
                                hourly_field.setChecked(true);
                                annual_field.setChecked(false);
                            }

                            //JSONArray skillarray = jarray.getJSONArray(c.getString("skillname"));
                            JSONArray jsarr = c.getJSONArray("skillname");
                            String arr = "";
                            JSONObject jo= jsarr.getJSONObject(0);
                          //  System.out.println("LEN=="+jo.length());
                         // subskills = toStringArray(jsarr);
                            StringBuilder sb = new StringBuilder();
                            for (int i=0;i<jo.length();i++){
                             //   System.out.println(jsarr);


                                sb.append(jo.getString(String.valueOf(i))).append(",");
                            }
                            arr = sb.deleteCharAt(sb.length() - 1).toString();

                            System.out.println("Arr=="+arr);

                            skillstext.setText(arr);
                            //skillstext.setBackgroundColor(Color.YELLOW);
//                            String arr = "";

//                            if (jsarr.length() > 0) {
//                                StringBuilder sb = new StringBuilder();
//
//                                for (String s : jsarr) {
//                                    sb.append(s).append(",");
//                                }
//
//                                arr = sb.deleteCharAt(sb.length() - 1).toString();
//                            }
                           // skillstext.setText(arr);
                           // setSkills(skillcatid);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.print(e);
                        }
                    }
                });


    }

    private void setSkills(final String scid)
    {
        String url = LOCATION_URL.SKILLS+scid;
        getResponse(Request.Method.GET, url, null,
                new EditJobs.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        JSONObject j = null;
                        try {

                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");
                            subskills = toStringArray(result);

//                    if(subskills.length > 0)
//                        skillstext.setText(subskills.toString());

//                            String arr = "";
//
//                            if (subskills.length > 0) {
//                                StringBuilder sb = new StringBuilder();
//
//                                for (String s : subskills) {
//                                    sb.append(s).append(",");
//                                }
//
//                                arr = sb.deleteCharAt(sb.length() - 1).toString();
//                            }
//
//                            System.out.println("STEXT=="+scid);
//                            skillstext.setText(arr);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void saveJob()
    {
        String cancel_req_tag = "savejob";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        int p = sindustry.getSelectedItemPosition();
        final String indus_id = imap_getid.get(String.valueOf(p));

        int cp = scategory.getSelectedItemPosition();
        final String cat_id = catmap.get(String.valueOf(cp));

        int rcp = reg_category_field.getSelectedItemPosition();

        if(String.valueOf(rcp).equals("0"))
            jtype = "Permanent";
        else if(String.valueOf(rcp).equals("1"))
            jtype = "Temporary";
        else if(String.valueOf(rcp).equals("2"))
            jtype = "Fixed Term Contract";

        final String titl = title_field.getText().toString();
        final String locat = loc_field.getText().toString();
        final String enddate = end_date_field.getText().toString();
        final String salmin = salary_min_field.getText().toString();
        final String salmax = salary_max_field.getText().toString();
        final String des = description_field.getText().toString();
        final String add = additional_field.getText().toString();


        if(annual_field.isChecked())
            sal_type = "annual";
        else
            sal_type = "hourly";

        //skillstext = (TextView) findViewById(R.id.skills);

        String url  = URL_UPDATE_JOB + jobid;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "save job Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        Intent intent=new Intent(getApplicationContext(),PostedJobs.class);
                        startActivity(intent);
                    }
                    else
                    {
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

                String skills = skillstext.getText().toString();
//                        System.out.println("TAGG=="+skillstext.getTag().toString());
                String[] skillar = skills.split(",");
                Gson gs = new Gson();
                String arr = gs.toJson(skillar);
                params.put("skills", skills);
                params.put("title", titl);
                params.put("description", des);
                params.put("location", locat);
                params.put("end_date", enddate);
                params.put("salary_min", salmin);
                params.put("salary_max", salmax);
                params.put("additional", add);
                params.put("industry_id", indus_id);
                params.put("skill_category_id", cat_id);
                params.put("salary_type", sal_type);
                params.put("type", jtype);
                return params;

            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);


    }
    private void getIndustry(){

        String url = LOCATION_URL.INDUSTRY;

        getResponse(Request.Method.GET, url, null,
                new EditJobs.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        JSONObject j = null;
                        try {
                            industryList = response;
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");

                            System.out.println("dddddddddddd"+result);

                            //  System.exit(0);

                            listIndustry(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // This check which radio button was clicked
        switch (view.getId()) {
            case R.id.annual:
                if (checked)
                    //Do something when radio button is clicked
                    salarytype = "annual";
                //   Toast.makeText(getApplicationContext(), "It seems like you feeal RelativeLayout easy", Toast.LENGTH_SHORT).show();
                break;

            case R.id.hourly:
                //Do something when radio button is clicked
                salarytype = "hourly";
                // Toast.makeText(getApplicationContext(), "It seems like you feel LinearLayout easy", Toast.LENGTH_SHORT).show();
                break;
        }
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
       //
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);

        return null;
    }

    private void getCategory() {
        //Creating a string request
        skillslist.clear();

        getResponse(Request.Method.GET, LOCATION_URL.CATEGORY, null,
                new EditJobs.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        JSONObject j = null;
                        try {
                            categoryList = response;

                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");
                            String categoryid = "";
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    //Getting json object
                                    JSONObject json = result.getJSONObject(i);
                                    //Adding the name of the student to array list
                                    categoryid = json.getString("id");
                                    catmap.put(String.valueOf(i),categoryid);
                                    catindex.put(categoryid,String.valueOf(i));
                                    categoryarray.add(json.getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditJobs.this,
                                    android.R.layout.simple_spinner_item, categoryarray);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            scategory.setAdapter(adapter);
                            //Setting adapter to show the items in the spinner
                          //  scategory.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.material_spinner, categoryarray));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }


    public void getResponse(int method, String url, JSONObject jsonValue, final EditJobs.VolleyCallback callback) {

        String cancel_req_tag = url;

        RequestQueue queue = AppSingleton.getInstance(EditJobs.this).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(EditJobs.this, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(EditJobs.this).addToRequestQueue(strreq, cancel_req_tag);
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


    private void listIndustry(JSONArray j){
        //Traversing through all the items in the json array

        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                String CityID =  json.getString(LOCATION_URL.TAG_id);
                imap.put(CityID,String.valueOf(i));
                imap_getid.put(String.valueOf(i),CityID);
                System.out.println("mmmid=="+CityID+"==val="+String.valueOf(i));
                industryarray.add(json.getString(LOCATION_URL.TAG_NAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       // industryarray.add(0, "Select Industry");
        //Setting adapter to show the items in the spinner
        sindustry.setAdapter(new ArrayAdapter<String>(EditJobs.this, android.R.layout.simple_spinner_item, industryarray));
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
