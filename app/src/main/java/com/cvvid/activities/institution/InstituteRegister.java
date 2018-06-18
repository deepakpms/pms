package com.cvvid.activities.institution;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.cvvid.activities.employer.EmployerBasicRegister;
import com.cvvid.activities.employer.EmployerProfile;
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

public class InstituteRegister extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private Button submitbtn = null;
    private static final String TAG = "RegisterActivity";
    private static final String URL_FREE_INSTITUTE = CONFIG_URL+"/createinstitution";
    private static final String URL_CHECK_EMAIL = CONFIG_URL+"/EmailVerify";

    AwesomeValidation validation;

    ProgressDialog progressDialog;
    SessionManager session;

    public String fname, sname, email, phoneno, name, address, postcode,city, county, country, no_of_pupils, password, confirmpassword;
    AutoCompleteTextView input_postcode;
    private EditText input_fname, input_sname, input_email, input_phoneno, input_name, input_address, input_city, input_county, input_country, input_no_of_pupils, input_password, input_confirmpassword;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    SharedPreferences sharedpreferences;
    public static final String RegisterPref = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_register);

        //        session manager
        session = new SessionManager(getApplicationContext());

        getSupportActionBar().setTitle("REGISTER YOUR INTEREST AS A SCHOOL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        input_fname = (EditText) findViewById(R.id.input_fname);
        input_sname = (EditText) findViewById(R.id.input_sname);
        input_email = (EditText) findViewById(R.id.input_email);
        input_phoneno = (EditText) findViewById(R.id.input_phoneno);
        input_name = (EditText) findViewById(R.id.input_name);
        input_address = (EditText) findViewById(R.id.input_address);
        input_postcode = (AutoCompleteTextView) findViewById(R.id.input_postcode);
        input_city = (EditText) findViewById(R.id.input_city);
        input_county = (EditText) findViewById(R.id.input_county);
        input_country = (EditText) findViewById(R.id.input_country);
        input_no_of_pupils = (EditText) findViewById(R.id.input_no_of_pupils);
        input_password = (EditText) findViewById(R.id.input_password);
        input_confirmpassword = (EditText) findViewById(R.id.input_confirmpassword);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        submitbtn = (Button) findViewById(R.id.btn_action);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fname = input_fname.getText().toString();
                sname = input_sname.getText().toString();
                email = input_email.getText().toString();
                phoneno = input_phoneno.getText().toString();
                name = input_name.getText().toString();
                address = input_address.getText().toString();
                postcode = input_postcode.getText().toString();
                city = input_city.getText().toString();
                county = input_county.getText().toString();
                country = input_country.getText().toString();
                password = input_password.getText().toString();
                confirmpassword  = input_confirmpassword.getText().toString();
                no_of_pupils  = input_no_of_pupils.getText().toString();


                validation = new AwesomeValidation(COLORATION);
                validation.setColor(Color.YELLOW);


                validation.addValidation(InstituteRegister.this, R.id.input_fname, RegexTemplate.NOT_EMPTY, R.string.fnameerr);
                validation.addValidation(InstituteRegister.this, R.id.input_sname, RegexTemplate.NOT_EMPTY, R.string.snameerr);
                validation.addValidation(InstituteRegister.this, R.id.input_email, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerr);
                validation.addValidation(InstituteRegister.this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.schnameerr);
                validation.addValidation(InstituteRegister.this, R.id.input_no_of_pupils, RegexTemplate.NOT_EMPTY, R.string.nopuperr);
                // to validate with a simple custom validator function

                if(validation.validate())
                {
                    register(name,fname, sname, email,phoneno,address,postcode, city, county,country,no_of_pupils,password);
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

    //register(name,fname, sname, email,address,postcode, city, county,country,no_of_pupils,password,confirmpassword);
    public void register(final String name,final String forenames,final String surname,final String email,final String phoneno,final String address,final String postal_code,final String locality,final String county,final String country,final String no_of_pupils,final String password)
    {
        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FREE_INSTITUTE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Institute Register Response: " + response.toString());
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

//                        String type = "institution_admin";

                        String user_id = c.getString("id");
                        // String institution_id = c.getString("institution_id");
                        String forenames = c.getString("forenames");
                        String surname = c.getString("surname");
                        String type = c.getString("type");
                    //    String profile_id = c.getString("profile_id");
                        String institution_id = c.getString("institution_id");
                        String username = forenames+" "+surname;
                        // Creating user login session
                        // Use user real data
                        session.createLoginSession(user_id, type, username, "0","0", institution_id);

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
                params.put("name", name);
                params.put("forenames", forenames);
                params.put("surname", surname);
                params.put("email", email);
                params.put("tel", phoneno);
                params.put("address", address);
                params.put("postcode", postal_code);
                params.put("town", locality);
                params.put("county", county);
                params.put("country", country);
                params.put("num_pupils", no_of_pupils);
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
