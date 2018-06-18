package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateBasicRegister;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.adaptors.candidate.PlaceArrayAdapter;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EmployerBasicRegister extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private Button submitbtn = null;
    private static final String TAG = "RegisterActivity";
    private static final String URL_FREE_EMPLOYER = CONFIG_URL+"/createemployer";
    private static final String URL_CHECK_EMAIL = CONFIG_URL+"/EmailVerify";
    boolean ispremium;
    AwesomeValidation validation;
    private JSONArray result;
    private ArrayList<String> industryarray;
    MaterialSpinner sindustry;
    public static String industryList="";
    public static String industry_id="";
    ProgressDialog progressDialog;
    SessionManager session;
    SharedPreferences sharedpreferences;
    AutoCompleteTextView input_postcode;
    private EditText input_name,input_web,input_tel,input_address,input_city,input_county,input_country,input_slug,
             input_fname,input_sname,input_current_job,input_email,input_password,input_confirmpassword;
    private String cname,fname,sname,web,tel,address,postcode,city,county,country,slug,job,email,pwd,cpwd;
    public static final String RegisterPref = "MyPrefs";

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_basic_register);

        industryarray = new ArrayList<String>();

        getIndustry();

//        session manager
        session = new SessionManager(getApplicationContext());

        input_name = (EditText) findViewById(R.id.input_name);
        input_web = (EditText) findViewById(R.id.input_web);
        input_tel = (EditText) findViewById(R.id.input_tel);
        input_sname = (EditText) findViewById(R.id.input_sname);
        input_fname = (EditText) findViewById(R.id.input_fname);
        input_postcode = (AutoCompleteTextView) findViewById(R.id.input_postcode);
        input_email= (EditText) findViewById(R.id.input_email);
        input_current_job= (EditText) findViewById(R.id.input_current_job);
        input_address= (EditText) findViewById(R.id.input_address);
        input_county= (EditText) findViewById(R.id.input_county);
        input_country= (EditText) findViewById(R.id.input_country);
        input_password= (EditText) findViewById(R.id.input_password);
        input_confirmpassword= (EditText) findViewById(R.id.input_confirmpassword);
        input_slug = (EditText) findViewById(R.id.input_slug);
        input_city = (EditText) findViewById(R.id.input_city);
        input_password = (EditText) findViewById(R.id.input_password);
        input_confirmpassword = (EditText) findViewById(R.id.input_confirmpassword);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // decalaration of variables
        sindustry = (MaterialSpinner)findViewById(R.id.reg_industry_id);

        Intent ie = getIntent();
        Bundle bd = ie.getExtras();

        ispremium = (Boolean)bd.get("ispremium");
        if(!ispremium)
            getSupportActionBar().setTitle("REGISTER AS A EMPLOYER");
        else
            getSupportActionBar().setTitle("REGISTER AS PREMIUM Employer");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void afterTextChanged(Editable s) {
                input_slug.setText(input_name.getText().toString());
            };
        });



        input_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    checkValidEmail();
                }
            }
        });



        submitbtn = (Button) findViewById(R.id.btn_action);

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


        submitbtn = (Button) findViewById(R.id.btn_action);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cname = input_name.getText().toString();
                fname = input_fname.getText().toString();
                sname = input_sname.getText().toString();
                email = input_email.getText().toString();
                job = input_current_job.getText().toString();
                address = input_address.getText().toString();
                postcode = input_postcode.getText().toString();
                city = input_city.getText().toString();
                county = input_county.getText().toString();
                country = input_country.getText().toString();
                slug = input_slug.getText().toString();
                pwd = input_password.getText().toString();
                cpwd  = input_confirmpassword.getText().toString();
                web = input_web.getText().toString();
                tel = input_tel.getText().toString();

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);


                validation.addValidation(EmployerBasicRegister.this, R.id.input_fname, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
                validation.addValidation(EmployerBasicRegister.this, R.id.input_sname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
                // to validate with a simple custom validator function
                validation.addValidation(EmployerBasicRegister.this, R.id.input_dob, new SimpleCustomValidation() {
                    @Override
                    public boolean compare(String input) {
                        // check if the age is >= 18
                        try {
                            Calendar calendarBirthday = Calendar.getInstance();
                            Calendar calendarToday = Calendar.getInstance();
                            calendarBirthday.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(input));
                            int yearOfToday = calendarToday.get(Calendar.YEAR);
                            int yearOfBirthday = calendarBirthday.get(Calendar.YEAR);
                            if (yearOfToday - yearOfBirthday > 18) {
                                return true;
                            } else if (yearOfToday - yearOfBirthday == 18) {
                                int monthOfToday = calendarToday.get(Calendar.MONTH);
                                int monthOfBirthday = calendarBirthday.get(Calendar.MONTH);
                                if (monthOfToday > monthOfBirthday) {
                                    return true;
                                } else if (monthOfToday == monthOfBirthday) {
                                    if (calendarToday.get(Calendar.DAY_OF_MONTH) >= calendarBirthday.get(Calendar.DAY_OF_MONTH)) {
                                        return true;
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }, R.string.doberr);

                // to validate the confirmation of another field
                String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
                validation.addValidation(EmployerBasicRegister.this, R.id.input_password, RegexTemplate.NOT_EMPTY, R.string.passerr);
                validation.addValidation(EmployerBasicRegister.this, R.id.input_confirmpassword, R.id.input_password, R.string.cpasserr);
                //validation.addValidation(CandidateBasicRegister.this, R.id.input_slug, RegexTemplate.NOT_EMPTY, R.string.slugerr);

                if(validation.validate())
                {
                    register(cname,fname, sname, email,web,tel, job, address,postcode, city, county,country,slug,pwd,cpwd);
                }

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView)findViewById(R.id
                .input_postcode);
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

            // Setup Geocoder
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;

            // Attempt to Geocode from place lat & long
            try {

                addresses = geocoder.getFromLocation(
                        place.getLatLng().latitude,
                        place.getLatLng().longitude,
                        1);

                if (addresses.size() > 0) {

                    // Here are some results you can geocode
                    String ZIP;
                    String city;
                    String state;
                    String country;

                    if (addresses.get(0).getPostalCode() != null) {
                        ZIP = addresses.get(0).getPostalCode();
                        //TextView input_postcode = (TextView)getView().findViewById(R.id.input_postcode);
                        input_postcode.setText(ZIP);
                        Log.d("ZIP", ZIP);
                    }

                    if (addresses.get(0).getLocality() != null) {
                        city = addresses.get(0).getLocality();
                        //TextView input_city = (TextView)getView().findViewById(R.id.input_city);
                        input_city.setText(city);
                        Log.d("city", city);
                    }

                    if (addresses.get(0).getAdminArea() != null) {
                        state = addresses.get(0).getAdminArea();
                        //TextView input_county = (TextView)getView().findViewById(R.id.input_county);
                        input_county.setText(state);
                        Log.d("state", state);
                    }

                    if (addresses.get(0).getCountryName() != null) {
                        country = addresses.get(0).getCountryName();
                        // TextView input_country = (TextView)getView().findViewById(R.id.input_country);
                        input_country.setText(country);
                        Log.d("country", country);
                    }

                }else{
                    input_postcode.setText("");
                    input_city.setText("");
                    input_county.setText("");
                    input_country.setText("");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            places.release();

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

    public void register(final String comp_name,final String forenames,final String surname,final String email,final String website,final String tele,final String current_job,final String address,final String postal_code,final String locality,final String county,final String country,final String slug,final String password,final String password_confirmation)
    {
        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FREE_EMPLOYER, new Response.Listener<String>() {

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
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        JSONArray jOb = jObj.getJSONArray("data");
                        JSONObject c = jOb.getJSONObject(0);

                        String type = "employer";

                        String user_id = c.getString("id");
                        // String institution_id = c.getString("institution_id");
                        String forenames = c.getString("forenames");
                        String surname = c.getString("surname");
                        String profile_id = c.getString("profile_id");
                        String employer_id = c.getString("employer_id");
                        String username = forenames+" "+surname;

                        // Creating user login session
                        // Use user real data
                        session.createLoginSession(user_id, type, username, profile_id,employer_id,"0");

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();


                        editor.putString("user_id", user_id); // Storing string
                        editor.putString("type", type); // Storing string
                        editor.putString("username", username); // Storing string

                        editor.commit(); // commit changes

                        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
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
                params.put("name", comp_name);
                params.put("forenames", forenames);
                params.put("surname", surname);
                params.put("website", website);
                params.put("tel", tele);
                params.put("email", email);
                params.put("industry_id", industry_id);

                if(!ispremium)
                    params.put("is_premium", "0");
                else
                    params.put("is_premium", "1");
                params.put("current_job", current_job);
                params.put("address", address);
                params.put("postcode", postal_code);
                params.put("town", locality);
                params.put("county", county);
                params.put("country", country);
                params.put("slug", slug);
                params.put("password", password);
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }


    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // This check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    //Do something when radio button is clicked
                  //  Toast.makeText(getApplicationContext(), "It seems like you feeal RelativeLayout easy", Toast.LENGTH_SHORT).show();
                break;

            case R.id.radioButton2:
                //Do something when radio button is clicked
             //   Toast.makeText(getApplicationContext(), "It seems like you feel LinearLayout easy", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getIndustry(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(LOCATION_URL.INDUSTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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


    private void listIndustry(JSONArray j){
        //Traversing through all the items in the json array

        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                String CityID =  json.getString(LOCATION_URL.TAG_id);

                industryarray.add(json.getString(LOCATION_URL.TAG_NAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        industryarray.add(0, "Select Industry");
        //Setting adapter to show the items in the spinner
        sindustry.setAdapter(new ArrayAdapter<String>(EmployerBasicRegister.this, android.R.layout.simple_spinner_item, industryarray));
    }

    public void checkValidEmail() {

        String cancel_req_tag = "Verify Email";
        progressDialog.setMessage("Checking email Availability..");
        showDialog();

        //Creating a string request

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_CHECK_EMAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status != 200)
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
                params.put("email", input_email.getText().toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
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
