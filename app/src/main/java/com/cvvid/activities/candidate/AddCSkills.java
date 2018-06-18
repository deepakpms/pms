package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class AddCSkills extends AppCompatActivity {

    ProgressDialog progressDialog;
    final ArrayList<AddCSkills.SkillsClass> skillslist = new ArrayList<AddCSkills.SkillsClass>();
    public static String skillsList="";
    private JSONArray result;
    TextView skillstext,catname;
    Button btn_action;
    private String USER_ID;
    public String[] skills;
    public boolean[] checkedskills;
    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_items);

        // Progress dialog
        progressDialog = new ProgressDialog(AddCSkills.this);
        progressDialog.setCancelable(false);
        RelativeLayout skillslayout = (RelativeLayout) findViewById(R.id.addskills);
        skillstext = (TextView)findViewById(R.id.skills);
        catname = (TextView)findViewById(R.id.scategory);


        // Session Manager
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // user_id
        USER_ID = user.get(SessionManager.USER_ID);

        btn_action = (Button) findViewById(R.id.add_skill);

        getLoadData();

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("DDD=="+skillstext.getText().toString());

                if(!skillstext.getText().toString().equals("Select Skills")) {
                    String cancel_req_tag = "career skills";
                    progressDialog.setMessage("Please wait a moment");
                    showDialog();

                    String url = CONFIG_URL + "/createcareer/id/" + USER_ID;
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "save hobby Response: " + response.toString());
                            hideDialog();

                            try {
                                System.out.println("EDD==" + response);

                                JSONObject jObj = new JSONObject(response);

                                int status = jObj.getInt("status");
                                String message = jObj.getString("message");
                                if (status == 200) {

                                    sendFragment();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                            String[] skillarr = skills.split(",");
                            Gson gs = new Gson();
                            String arr = gs.toJson(skillarr);
                            params.put("skills", skills);
//                        params.put("proficiency", prof);
                            return params;

                        }

                    };

                    strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    // Adding request to request queue
                    AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
                }
                else{
                    Toast.makeText(AddCSkills.this,
                            "Please Select Skills", Toast.LENGTH_LONG).show();
                }

            }
        });

        skillslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCSkills.this);

                int count  = skills.length;
                checkedskills  = new boolean[count];

                // make a list to hold state of every color
                for (int i = 0; i < skills.length; i++) {
                    AddCSkills.SkillsClass EatingClass = new AddCSkills.SkillsClass();
                    EatingClass.setName(skills[i]);
                    EatingClass.setId(skills[i]);
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
//                                //checkedskills[i] = false;
//                                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
//                                skillslist.get(which).setSelected(isChecked);
////                                skillslist.get(i).setSelected(false);
//
//                            }
//
//
//                        }else{
//                            checkedskills[0] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                            skillslist.get(which).setSelected(isChecked);
//                            skillslist.get(0).setSelected(false);
                        //}
                    }
                });

                builder.setCancelable(false);

                builder.setTitle("Select Career Paths");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skillstext.setText("");

                        // save state of selected vos
                        ArrayList<AddCSkills.SkillsClass> selectedList = new ArrayList<>();
                        // System.out.println("select"+skillslist.size());
                        for (int i = 0; i < skillslist.size(); i++) {
                            AddCSkills.SkillsClass EatingClass = skillslist.get(i);
                            skills[i] = EatingClass.getName();
                            checkedskills[i] = EatingClass.isSelected();


                            if (EatingClass.isSelected()) {
                                selectedList.add(EatingClass);
                            }
                        }



                        for (int i = 0; i < selectedList.size(); i++) {
                            // if element is last then not attach comma or attach it


                            if (i != selectedList.size() - 1) {
                                skillstext.setText(skillstext.getText() + selectedList.get(i).getName() + ",");

                            }
                            else {
                                skillstext.setText(skillstext.getText() + selectedList.get(i).getName());
                            }
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
        });

    }


    private void getLoadData()
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        final String cat_id = getIntent().getExtras().getString("category_id");

        String URL_FOR_DETAIL = CONFIG_URL+"/getskillsubcategory/id/" + cat_id;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                hideDialog();
                JSONObject j = null;
                try {

                    catname.setText(getIntent().getExtras().getString("category_name"));

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

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(AddCSkills.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        AppSingleton.getInstance(AddCSkills.this).addToRequestQueue(strReq, cancel_req_tag);


    }
    public static String[] toStringArray(JSONArray array) throws JSONException {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        List<String> list = new ArrayList<String>();
        for(int i=0; i<arr.length; i++) {
            list.add(array.getJSONObject(i).getString("name"));
//            list.add();
        }
        String[] stringArray = list.toArray(new String[list.size()]);
        // System.out.println("sssss"+stringArray.toString());
        return stringArray;
    }

    private class SkillsClass {

        private String id;
        private String name;
        private boolean selected;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
        intent.putExtra("position","3");
        intent.putExtra("issetting","0");
        startActivity(intent);

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
