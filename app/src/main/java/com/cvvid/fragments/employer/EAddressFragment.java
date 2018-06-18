package com.cvvid.fragments.employer;


import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class EAddressFragment extends Fragment  implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    // Session Manager Class
    SessionManager session;

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID =1;
    private AutoCompleteTextView mAutocompleteTextView,input_postcode;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String EMPLOYER_ID;
    private EditText input_saddress,input_city,input_county,input_country;
    private Button btn_save;

    private double latitude;
    private double longitude;

    ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));



    public EAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_caddress, container, false);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

        input_saddress = (EditText) view.findViewById(R.id.input_saddress);
        input_postcode = (AutoCompleteTextView) view.findViewById(R.id.input_postcode);
        input_city = (EditText) view.findViewById(R.id.input_city);
        input_county = (EditText) view.findViewById(R.id.input_county);
        input_country = (EditText) view.findViewById(R.id.input_country);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        EMPLOYER_ID = user.get(SessionManager.EMPLOYER_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        getLoadData(USER_ID, view);

        //save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String address = input_saddress.getText().toString();
                String postcode = input_postcode.getText().toString();
                String city = input_city.getText().toString();
                String county = input_county.getText().toString();
                String country = input_country.getText().toString();

                saveChanges(USER_ID, address, postcode, city, county, country);
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                //.enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView)view.findViewById(R.id
                .input_postcode);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


        return view;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            // Setup Geocoder
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getContext(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private void saveChanges(final String USER_ID, final String address, final String postcode, final String city, final String county, final String country)
    {
        String cancel_req_tag = "Save Changes";
        progressDialog.setMessage("loading...");
        showDialog();

        //Creating a string request
        String URL_FOR_DETAIL =CONFIG_URL+"/updatelocation/id/" + USER_ID+"/type/"+USER_TYPE;

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
                        Toast.makeText(getContext(),"Successfully Saved", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("address", address);
                params.put("postcode", postcode);
                params.put("town", city);
                params.put("county", county);
                params.put("country", country);

                return params;

            }



        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }


    private void getLoadData(final String USER_ID, final View view)
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = CONFIG_URL+"/fetchemployerprofiledetails/id/" + USER_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    EditText sddress = (EditText) view.findViewById(R.id.input_saddress);
                    sddress.setText(c.getString("address"));
                    EditText postcode = (EditText) view.findViewById(R.id.input_postcode);
                    postcode.setText(c.getString("postcode"));
                    EditText city = (EditText) view.findViewById(R.id.input_city);
                    city.setText(c.getString("town"));
                    EditText county = (EditText) view.findViewById(R.id.input_county);
                    county.setText(c.getString("county"));
                    EditText country = (EditText) view.findViewById(R.id.input_country);
                    country.setText(c.getString("country"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);

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
