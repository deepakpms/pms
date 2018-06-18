package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.cvvid.adaptors.candidate.PlaceArrayAdapter;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class AddEducation extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks  {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

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

    MaterialSpinner completion_month,completion_year,level_spinner;
    AutoCompleteTextView location_field;
    EditText inst_field,study_field,grade_field,desc_field;
    String institution,loc,field,grade,desc,cmonth,cyear,level,USER_ID;
    // Session Manager Class
    SessionManager session;
    public static final String URL_SAVE_EDUCATION = CONFIG_URL+"/createeducation/id/";

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_education);

        Button btn_save = (Button) findViewById(R.id.btn_save_pexp);
        displayname = (TextView) findViewById(R.id.displayname);
        name = (EditText)findViewById(R.id.input_name);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE) ;


        completion_month = (MaterialSpinner) findViewById(R.id.completion_month);
        completion_year = (MaterialSpinner) findViewById(R.id.completion_year);
        level_spinner = (MaterialSpinner) findViewById(R.id.level);
        inst_field = (EditText) findViewById(R.id.institution);
        location_field = (AutoCompleteTextView) findViewById(R.id.location);
        study_field = (EditText) findViewById(R.id.field);
        grade_field = (EditText) findViewById(R.id.grade);
        desc_field = (EditText) findViewById(R.id.description);

        ArrayList<Integer> year = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int c_year = calendar.get(Calendar.YEAR);

        for (int i = 1958; i <= c_year; i++) {
            year.add(i);
        }

        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<Integer>(AddEducation.this, android.R.layout.simple_spinner_item, year);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        completion_year.setAdapter(adapter);

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

        validation.addValidation(AddEducation.this, R.id.institution, RegexTemplate.NOT_EMPTY, R.string.institution);


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
                    cmonth = getMonth(completion_month.getSelectedItem().toString());
                    cyear = completion_year.getSelectedItem().toString();
                    level = level_spinner.getSelectedItem().toString();
                    institution = inst_field.getText().toString();
                    loc = location_field.getText().toString();
                    field = study_field.getText().toString();
                    grade = grade_field.getText().toString();
                    desc = desc_field.getText().toString();

                    saveEducation(cmonth,cyear,level,institution,loc,field,grade,desc);
                }

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView)findViewById(R.id
                .location);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//            input_postcode.setFocusable(false);

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getApplicationContext(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    public void saveEducation(final String cmonth,final String cyear,final String level, final String institution,final String loc, final String field,final  String grade, final  String desc )
    {
        String cancel_req_tag = "education";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = URL_SAVE_EDUCATION + USER_ID;
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
                params.put("institution", institution);
                params.put("location", loc);
                params.put("field", field);
                params.put("level", level);
                params.put("completion_month", cmonth );
                params.put("completion_year", cyear);
                params.put("grade", grade);
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

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
        intent.putExtra("position","5");
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
