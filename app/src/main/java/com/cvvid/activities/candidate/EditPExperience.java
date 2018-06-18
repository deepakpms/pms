package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EditPExperience extends AppCompatActivity {

    String imagepath;
    File sourceFile;
    long totalSize = 0;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private String filePath = null;
    private String isFile = null;
    Context context;

    private EditText name;
    private JSONArray result;
    private ArrayList<String> categoryarray;
    MaterialSpinner scategory;

    private ArrayList<String> publisharray;
    MaterialSpinner spublish;

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "CVVid Professional Experience";

    // LogCat tag
    private static final String TAG = AddPExperience.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private TextView displayname;

    private Uri fileUri; // file url to store image/video

    AwesomeValidation validation;


    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;

    MaterialSpinner startmonth,startyear,endmonth,endyear;
    EditText position,location,company,description;
    String start_month,start_year,end_month,end_year,loc,pos,desc,company_name,USER_ID,pexpid;
    // Session Manager Class
    SessionManager session;
    ArrayList<Integer> contacts;
    public static final String URL_GET_PEXPERIENCE = CONFIG_URL+"/fetchprofessional/id/";
    public static final String URL_UPDATE_PEXPERIENCE = CONFIG_URL+"/updateprofessionalexprience/id/";
    ArrayAdapter<Integer> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pexperience);

        Button btn_save = (Button) findViewById(R.id.btn_save_pexp);
        displayname = (TextView) findViewById(R.id.displayname);
        name = (EditText)findViewById(R.id.input_name);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);
        displayname.setText("Edit Professional Experience");
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE) ;


        startmonth = (MaterialSpinner) findViewById(R.id.startmonth);
        startyear = (MaterialSpinner) findViewById(R.id.startyear);
        endmonth = (MaterialSpinner) findViewById(R.id.endmonth);
        endyear = (MaterialSpinner) findViewById(R.id.endyear);
        position = (EditText) findViewById(R.id.position);
        location = (AutoCompleteTextView) findViewById(R.id.location);
        company = (EditText) findViewById(R.id.company);
        description = (EditText) findViewById(R.id.description);

        contacts = new ArrayList<Integer>();

        for (int i = 1997; i < 2017; i++) {
            contacts.add(i);
        }

        adapter =
                new ArrayAdapter<Integer>(EditPExperience.this, android.R.layout.simple_spinner_item, contacts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startyear.setAdapter(adapter);
        endyear.setAdapter(adapter);
        Intent i = getIntent();
        pexpid = i.getStringExtra("pexpid");
        setValues(pexpid);
//        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);

        categoryarray = new ArrayList<String>();
        publisharray = new ArrayList<String>();

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        validation.addValidation(EditPExperience.this, R.id.company, RegexTemplate.NOT_EMPTY, R.string.company);



        /**
         * back video button click event
         */
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Choose video
                sendFragment();
            }
        });

        /**
         * save video button click event
         */
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validation.validate())
                {
                    start_month = getMonth(startmonth.getSelectedItem().toString());

                    start_year = startyear.getSelectedItem().toString();
                    end_month = getMonth(endmonth.getSelectedItem().toString());
                    end_year = endyear.getSelectedItem().toString();
                    loc = location.getText().toString();
                    pos = position.getText().toString();
                    desc = description.getText().toString();
                    company_name = company.getText().toString();


                    updatePExperience(start_month,start_year,end_month,end_year,company_name,loc,pos,desc);
                }

            }
        });


    }

    private void setValues(String pexp)
    {
        String cancel_req_tag = "edit";

        String URL_FOR_DETAIL = URL_GET_PEXPERIENCE + pexp;
        System.out.println("URLL==="+URL_FOR_DETAIL);
        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    String mstart = c.getString("startmonth");
                    String ystart = c.getString("startyear");

                    String mend = c.getString("endmonth");
                    String yend = c.getString("endyear");

                    String pos = c.getString("position");
                    String loc = c.getString("location");
                    String cn = c.getString("company_name");
                    String desc = c.getString("description");

                    position.setText(pos);
                    location.setText(loc);
                    company.setText(cn);
                    description.setText(desc);
                    //int spinnerPosition = adapter.getPosition(Integer.parseInt(getMonth(mstart)));

                   // System.out.println("start yeaer=="+contacts.indexOf(ystart));

                    try {
                        if(mstart != "")
                            startmonth.setSelection(Integer.parseInt(mstart)-1);
                        if(mend != "")
                            endmonth.setSelection(Integer.parseInt(mend)-1);

                        //ArrayAdapter myAdap1 = (ArrayAdapter) startyear.getAdapter(); //cast to an ArrayAdapter
                        // int spinnerPosition1 = adapter.getPosition(ystart);
                        for (int i = 0; i < contacts.size(); i++) {
                            if (contacts.get(i) == Integer.parseInt(ystart))
                                startyear.setSelection(i);

                            if (contacts.get(i) == Integer.parseInt(yend))
                                endyear.setSelection(i);
                        }

                    }catch (NumberFormatException e){
                      //  System.out.println("not a number");
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
                Toast.makeText(EditPExperience.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(EditPExperience.this).addToRequestQueue(strReq, cancel_req_tag);
    }

    public void updatePExperience(final String smonth,final String syear,final String emonth, final String eyear,final String company_name, final String loc,final  String pos, final  String desc )
    {
        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = URL_UPDATE_PEXPERIENCE + pexpid;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        sendFragment();
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
                params.put("start_month", start_month);
                params.put("start_year", start_year);
                params.put("end_month", end_month);
                params.put("end_year", end_year);
                params.put("is_current", "0" );
                params.put("position", pos);
                params.put("company_name", company_name);
                params.put("location", loc);
                params.put("description", desc);
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }
    private int getIndex(MaterialSpinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
        intent.putExtra("issetting","0");
        intent.putExtra("position","4");
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

    private String getMonth(String month)
    {
        if(month.equalsIgnoreCase("January"))
            return "1";
        if(month.equalsIgnoreCase("February"))
            return "2";
        if(month.equalsIgnoreCase("March"))
            return "3";
        if(month.equalsIgnoreCase("April"))
            return "4";
        if(month.equalsIgnoreCase("May"))
            return "5";
        if(month.equalsIgnoreCase("June"))
            return "6";
        if(month.equalsIgnoreCase("July"))
            return "7";
        if(month.equalsIgnoreCase("August"))
            return "8";
        if(month.equalsIgnoreCase("September"))
            return "9";
        if(month.equalsIgnoreCase("October"))
            return "10";
        if(month.equalsIgnoreCase("November"))
            return "11";
        if(month.equalsIgnoreCase("December"))
            return "12";

        return "";

    }
}
