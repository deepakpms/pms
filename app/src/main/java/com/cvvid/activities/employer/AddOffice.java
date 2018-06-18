package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.cvvid.R;
import com.cvvid.activities.candidate.AddPExperience;
import com.cvvid.activities.candidate.CandidateProfile;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class AddOffice extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView, postcode;

    private EditText address,town,county,country;

    private double latitude,longitude;

    private AppCompatCheckBox head_office;

    private String headoffice = "0";

    private Button btn_create;
    ProgressDialog progressDialog;
    AwesomeValidation validation;


    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String EMP_ID;

    // Session Manager Class
    SessionManager session;

    private static final String TAG = AddOffice.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office);

        postcode = (AutoCompleteTextView) findViewById(R.id.postcode);
        address = (EditText) findViewById(R.id.address);
        town = (EditText) findViewById(R.id.town);
        county = (EditText) findViewById(R.id.county);
        country = (EditText) findViewById(R.id.country);
        btn_create = (Button)findViewById(R.id.btn_create);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);

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


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();


        head_office = (AppCompatCheckBox)findViewById(R.id.head_office);

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        validation.addValidation(AddOffice.this, R.id.postcode, RegexTemplate.NOT_EMPTY, R.string.posterr);

        head_office.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(head_office.isChecked()){
                    headoffice = "1";
                }else{
                    headoffice = "0";
                }
            }
        });

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


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveoffice();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView)findViewById(R.id
                .postcode);
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

                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                addresses = geocoder.getFromLocation(
                        place.getLatLng().latitude,
                        place.getLatLng().longitude,
                        1);

                if (addresses.size() > 0) {

                    // Here are some results you can geocode
                    String ZIP;
                    String city;
                    String state;
                    String coun;

                    if (addresses.get(0).getPostalCode() != null) {
                        ZIP = addresses.get(0).getPostalCode();
                        //TextView input_postcode = (TextView)getView().findViewById(R.id.input_postcode);
                        postcode.setText(ZIP);
                        Log.d("ZIP", ZIP);
                    }

                    if (addresses.get(0).getLocality() != null) {
                        city = addresses.get(0).getLocality();
                        //TextView input_city = (TextView)getView().findViewById(R.id.input_city);
                        town.setText(city);
                        Log.d("city", city);
                    }

                    if (addresses.get(0).getAdminArea() != null) {
                        state = addresses.get(0).getAdminArea();
                        //TextView input_county = (TextView)getView().findViewById(R.id.input_county);
                        county.setText(state);
                        Log.d("state", state);
                    }

                    if (addresses.get(0).getCountryName() != null) {
                        coun = addresses.get(0).getCountryName();
                        // TextView input_country = (TextView)getView().findViewById(R.id.input_country);
                        country.setText(coun);
                        Log.d("country", coun);
                    }

                }else{
                    postcode.setText("");
                    town.setText("");
                    county.setText("");
                    country.setText("");
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

    private void saveoffice()
    {
        if(validation.validate())
        {
           final String starr = address.getText().toString();
           final String pos = postcode.getText().toString();
           final String city = town.getText().toString();
           final String coun = county.getText().toString();
           final String count = country.getText().toString();

            String cancel_req_tag = "Save Changes";
            progressDialog.setMessage("loading...");
            showDialog();

            //Creating a string request
            String URL_FOR_DETAIL =CONFIG_URL+"/addofficelocation/id/" + EMP_ID;

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_FOR_DETAIL, new Response.Listener<String>() {

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
                            Toast.makeText(getApplicationContext(),"Successfully Saved", Toast.LENGTH_LONG).show();
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
                    params.put("address", starr);
                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    params.put("postcode", pos);
                    params.put("town", city);
                    params.put("county", coun);
                    params.put("country", count);
                    params.put("head_office", headoffice);
                    return params;
                }
            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
        }
    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
        intent.putExtra("issetting","0");
        intent.putExtra("position","3");
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
