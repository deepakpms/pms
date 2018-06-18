package com.cvvid.activities.candidate;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class CandidateBasicRegister extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private Button submitbtn = null;
    private static final String TAG = "RegisterActivity";
    private static final String URL_FREE_CANDIDATE = CONFIG_URL+"/createcandidate";
    private static final String URL_CHECK_EMAIL = CONFIG_URL+"/EmailVerify";

    boolean ispremium;
    AwesomeValidation validation;
    public static Integer dday,dmonth,dyear;
    Calendar myCalendar = Calendar.getInstance();
    public static int age;
    AutoCompleteTextView input_postcode;
    public String rforenames, rsurname, remail, rcurrent_job, raddress,rpostal_code, rlocality, rcounty,rcountry,rslug,rpassword,rpassword_confirmation, rdob;
    private EditText input_fname, input_sname, input_dob, input_email,input_currentjob,input_address, input_city,input_county,input_country,input_slug,input_password,input_confirmpassword;
    ProgressDialog progressDialog;
    SessionManager session;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    SharedPreferences sharedpreferences;
    public static final String RegisterPref = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_basic_register);

//        session manager
        session = new SessionManager(getApplicationContext());

        input_dob = (EditText) findViewById(R.id.input_dob);
        input_sname = (EditText) findViewById(R.id.input_sname);
        input_fname = (EditText) findViewById(R.id.input_fname);
        input_postcode = (AutoCompleteTextView) findViewById(R.id.input_postcode);
        input_email= (EditText) findViewById(R.id.input_email);
        input_currentjob= (EditText) findViewById(R.id.input_currentjob);
        input_address= (EditText) findViewById(R.id.input_address);
        input_county= (EditText) findViewById(R.id.input_county);
        input_country= (EditText) findViewById(R.id.input_country);
        input_password= (EditText) findViewById(R.id.input_password);
        input_confirmpassword= (EditText) findViewById(R.id.input_confirmpassword);
        input_slug = (EditText) findViewById(R.id.input_slug);
        input_city = (EditText) findViewById(R.id.input_city);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                dday=dayOfMonth;
                dmonth=monthOfYear+1;
                dyear=year;
                updateLabel();
                updateDisplay();
            }

        };

        input_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CandidateBasicRegister.this, date, myCalendar
                        .get(Calendar.YEAR)-16, myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        Intent ie = getIntent();
        Bundle bd = ie.getExtras();

        ispremium = (Boolean)bd.get("ispremium");
        if(!ispremium)
            getSupportActionBar().setTitle("REGISTER AS A JOB SEEKER");
        else
            getSupportActionBar().setTitle("REGISTER AS PREMIUM JOB SEEKER");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        input_sname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void afterTextChanged(Editable s) {
                if(!input_fname.getText().toString().equals(""))
                   input_slug.setText(input_fname.getText().toString() + "-" + input_sname.getText().toString());
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
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rforenames = input_fname.getText().toString();
                rsurname = input_sname.getText().toString();
                remail = input_email.getText().toString();
                rcurrent_job = input_currentjob.getText().toString();
                raddress = input_address.getText().toString();
                rpostal_code = input_postcode.getText().toString();
                rlocality = input_city.getText().toString();
                rcounty = input_county.getText().toString();
                rcountry = input_country.getText().toString();
                rslug = input_slug.getText().toString();
                rpassword = input_password.getText().toString();
                rpassword_confirmation  = input_confirmpassword.getText().toString();
                rdob = input_dob.getText().toString();

                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);


                validation.addValidation(CandidateBasicRegister.this, R.id.input_fname, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
                validation.addValidation(CandidateBasicRegister.this, R.id.input_sname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
                validation.addValidation(CandidateBasicRegister.this, R.id.input_email, Patterns.EMAIL_ADDRESS, R.string.inemail);
                validation.addValidation(CandidateBasicRegister.this, R.id.input_slug, RegexTemplate.NOT_EMPTY, R.string.slugerr);
                validation.addValidation(CandidateBasicRegister.this, R.id.input_postcode, RegexTemplate.NOT_EMPTY, R.string.posterr);

                // to validate with a simple custom validator function
                validation.addValidation(CandidateBasicRegister.this, R.id.input_dob, new SimpleCustomValidation() {
                    @Override
                    public boolean compare(String input) {
                        // check if the age is >= 18
                        try {
                            Calendar calendarBirthday = Calendar.getInstance();
                            Calendar calendarToday = Calendar.getInstance();
                            calendarBirthday.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(input));
                            int yearOfToday = calendarToday.get(Calendar.YEAR);
                            int yearOfBirthday = calendarBirthday.get(Calendar.YEAR);
                            if (yearOfToday - yearOfBirthday > 16) {
                                return true;
                            } else if (yearOfToday - yearOfBirthday == 16) {
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
              //  String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";

                String regexPassword = ".{6,}";
                validation.addValidation(CandidateBasicRegister.this, R.id.input_password, Pattern.compile(regexPassword), R.string.passerr);
                validation.addValidation(CandidateBasicRegister.this, R.id.input_confirmpassword, R.id.input_password, R.string.cpasserr);
                //validation.addValidation(CandidateBasicRegister.this, R.id.input_slug, RegexTemplate.NOT_EMPTY, R.string.slugerr);

                if(validation.validate())
                {
                    register(rforenames, rsurname, remail, rcurrent_job, raddress,rpostal_code, rlocality, rcounty,rcountry,rslug,rpassword,rpassword_confirmation, rdob);
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

    public void register(final String rforenames,final String rsurname,final String remail,final String rcurrent_job,final String raddress,final String rpostal_code,final String rlocality,final String rcounty,final String rcountry,final String rslug,final String rpassword,final String rpassword_confirmation,final String rdob)
    {
        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FREE_CANDIDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = "Successfully Registered!";
                    if(status == 200)
                    {
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        JSONArray jOb = jObj.getJSONArray("data");
                        JSONObject c = jOb.getJSONObject(0);

                        String type = "candidate";

//                                    if(type.equals("candidate")){

                        String user_id = c.getString("id");
                        // String institution_id = c.getString("institution_id");
                        String forenames = c.getString("forenames");
                        String surname = c.getString("surname");
                        String profile_id = c.getString("profile_id");

                        String username = forenames+" "+surname;

                        // Creating user login session
                        // Use user real data
                        session.createLoginSession(user_id, type, username, profile_id, "0", "0");

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();


                        editor.putString("user_id", user_id); // Storing string
                        editor.putString("type", type); // Storing string
                        editor.putString("username", username); // Storing string


                        editor.commit(); // commit changes

                        Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
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
                params.put("forenames", rforenames);
                params.put("surname", rsurname);
                params.put("email", remail);
                if(!ispremium)
                    params.put("is_premium", "0");
                else
                    params.put("is_premium", "1");
                params.put("current_job", rcurrent_job);
                params.put("dob", rdob);
                params.put("address", raddress);
                params.put("postcode", rpostal_code);
                params.put("town", rlocality);
                params.put("county", rcounty);
                params.put("country", rcountry);
                params.put("slug", rslug);
                params.put("password", rpassword);
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

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


    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // This check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    //Do something when radio button is clicked
                 //   Toast.makeText(getApplicationContext(), "It seems like you feeal RelativeLayout easy", Toast.LENGTH_SHORT).show();
                break;

            case R.id.radioButton2:
                //Do something when radio button is clicked
               // Toast.makeText(getApplicationContext(), "It seems like you feel LinearLayout easy", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateLabel() {
        input_dob.setText(dday + "/" + (dmonth +1)  + "/" + dyear);
    }

    public void updateDisplay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        age = year - dyear;
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
