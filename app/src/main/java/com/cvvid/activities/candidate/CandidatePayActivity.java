package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.YOUR_PUBLISHABLE_KEY_TEST_HERE;

public class CandidatePayActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;

    Stripe stripe;
    Integer amount;
    String name,id,price_suffix,description,cvc;
    Card card;
    Token tok;
    TextView price;
    ProgressDialog progressDialog;
    private static final String TAG = CandidatePayActivity.class.getSimpleName();
    private static final String URL_FOR_PAY = CONFIG_URL+"/upgradestore/id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_pay);

        //        session
        session = new SessionManager(CandidatePayActivity.this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        amount = extras.getInt("plan_price");
        name = extras.getString("plan_name");
        id = extras.getString("plan_id");
        price_suffix = extras.getString("price_suffix");
        description = extras.getString("description");

        price = (TextView)findViewById(R.id.item_price);

        price.setText("$ "+amount+" "+price_suffix);

        try {
            stripe = new Stripe(YOUR_PUBLISHABLE_KEY_TEST_HERE);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

    }


    public void submitCard(View view) {
        // TODO: replace with your own test key
        TextView cardNameField = (TextView)findViewById(R.id.input_cname);
        TextView cardNumberField = (TextView) findViewById(R.id.input_cnumber);
        TextView monthField = (TextView) findViewById(R.id.input_month);
        TextView yearField = (TextView) findViewById(R.id.input_year);
        TextView cvcField = (TextView) findViewById(R.id.input_cvc);

        cvc = cvcField.getText().toString();

        card = new Card(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        );

        card.setCurrency("usd");
        card.setName(cardNameField.getText().toString());
        card.setAddressZip("1000");

        stripe.createToken(card, YOUR_PUBLISHABLE_KEY_TEST_HERE, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                tok = token;
                new StripeCharge(token.getId()).execute();

            }

            public void onError(Exception error) {

                Toast.makeText(getApplicationContext(), "Token created: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("Stripe", error.getLocalizedMessage());
            }
        });
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token;
        ProgressDialog pd;


        public StripeCharge(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CandidatePayActivity.this);
            pd.setMessage("Working ...");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    postData(id,token,cvc);
                }
            }.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result",s);

                pd.dismiss(); }

    }


    public void postData(final String plan_id, final String token, final String cvc) {
        // Create a new HttpClient and Post Header

        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_PAY + USER_ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //  hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        sendFragment();
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
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
                // hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("plan_id", plan_id);
                params.put("token", token);
                params.put("cvc", cvc);
                //   System.out.println("params--"+params.toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        Log.e("Query",result.toString());
        return result.toString();
    }

    public class NameValuePair{
        String name,value;

        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

     private  void sendFragment()
     {
         Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
         intent.putExtra("issetting","1");
         intent.putExtra("position","2");
         startActivity(intent);
    }
}
