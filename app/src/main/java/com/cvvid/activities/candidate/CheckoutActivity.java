package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.CardBackFragment;
import com.cvvid.CardFrontFragment;
import com.cvvid.R;
import com.cvvid.Utils.CreditCardUtils;
import com.cvvid.Utils.ViewPagerAdapter;
import com.cvvid.activities.employer.EmployerProfile;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CCNameFragment;
import com.cvvid.fragments.candidate.CCNumberFragment;
import com.cvvid.fragments.candidate.CCSecureCodeFragment;
import com.cvvid.fragments.candidate.CCValidityFragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.YOUR_PUBLISHABLE_KEY_TEST_HERE;

public class CheckoutActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener {

    @BindView(R.id.btnNext)
    Button btnNext;

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;

    Stripe stripe;
    Card card;
    Token tok;

    Integer amount;

    public CardFrontFragment cardFrontFragment;
    public CardBackFragment cardBackFragment;

    //This is our viewPager
    private ViewPager viewPager;

    public CCNumberFragment numberFragment;
    public CCNameFragment nameFragment;
    public CCValidityFragment validityFragment;
    public CCSecureCodeFragment secureCodeFragment;

    int total_item;
    boolean backTrack = false;

    private boolean mShowingBack = false;

    ProgressDialog progressDialog;
    String name,id,price_suffix,description,cvc,type;
    private static final String TAG = CheckoutActivity.class.getSimpleName();
    private static final String URL_FOR_CPAY = CONFIG_URL+"/upgradestore/id/";
    private static final String URL_FOR_EPAY = CONFIG_URL+"/upgradestoreemployer/id/";

    String cardNumber, cardCVV, cardValidity, cardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ButterKnife.bind(this);


        cardFrontFragment = new CardFrontFragment();
        cardBackFragment = new CardBackFragment();

        if (savedInstanceState == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cardFrontFragment).commit();

        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        //        session
        session = new SessionManager(CheckoutActivity.this);

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
        type = extras.getString("type");
        name = extras.getString("plan_name");
        id = extras.getString("plan_id");
        price_suffix = extras.getString("price_suffix");
        description = extras.getString("description");

        try {
            stripe = new Stripe(YOUR_PUBLISHABLE_KEY_TEST_HERE);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == total_item)
                    btnNext.setText("SUBMIT");
                else
                    btnNext.setText("NEXT");

                Log.d("track", "onPageSelected: " + position);

                if (position == total_item) {
                    flipCard();
                    backTrack = true;
                } else if (position == total_item - 1 && backTrack) {
                    flipCard();
                    backTrack = false;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager.getCurrentItem();
                if (pos < total_item) {
                    viewPager.setCurrentItem(pos + 1);
                } else {
                    checkEntries();
                }

            }
        });

    }

    public void checkEntries() {

        cardName = nameFragment.getName();
        cardNumber = numberFragment.getCardNumber();
        cardValidity = validityFragment.getValidity();
        cardCVV = secureCodeFragment.getValue();

        String currentString = cardValidity;
        String[] separated = currentString.split("/");
        String month = separated[0];
        String year = separated[1];

        if (TextUtils.isEmpty(cardName)) {
            Toast.makeText(CheckoutActivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardNumber) || !CreditCardUtils.isValid(cardNumber.replace(" ",""))) {
            Toast.makeText(CheckoutActivity.this, "Enter Valid card number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardValidity)||!CreditCardUtils.isValidDate(cardValidity)) {
            Toast.makeText(CheckoutActivity.this, "Enter correct validity", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardCVV)||cardCVV.length()<3) {
            Toast.makeText(CheckoutActivity.this, "Enter valid security number", Toast.LENGTH_SHORT).show();
        } else
            sendCard(cardName, cardNumber, month,year, cardCVV);
           // Toast.makeText(CheckoutActivity.this, "Your card is added", Toast.LENGTH_SHORT).show();
    }

    private void sendCard(final String name, final String number, final String month, final String year, final String cvvn)
    {
        card = new Card(
                number,
                Integer.valueOf(month),
                Integer.valueOf(year),
                cvvn
        );

        cvc = cvvn;
        card.setCurrency("usd");
        card.setName(name);
        card.setAddressZip("1000");

        stripe.createToken(card, YOUR_PUBLISHABLE_KEY_TEST_HERE, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                tok = token;
                new CheckoutActivity.StripeCharge(token.getId()).execute();

            }

            public void onError(Exception error) {

                Toast.makeText(getApplicationContext(), "Token created: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("Stripe", error.getLocalizedMessage());
            }
        });
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token;
        String cvv;
        ProgressDialog pd;


        public StripeCharge(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CheckoutActivity.this);
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
        String url = "";
        if(type.equals("candidate"))
            url = URL_FOR_CPAY+USER_ID;
        else
            url = URL_FOR_EPAY+USER_ID;




        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

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
                        if(type.equals("candidate"))
                            sendFragment();
                        else
                            sendEFragment();
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


    private String getQuery(List<CheckoutActivity.NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (CheckoutActivity.NameValuePair pair : params)
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

    private  void sendEFragment()
    {
        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
        intent.putExtra("issetting","1");
        intent.putExtra("position","0");
        startActivity(intent);
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        numberFragment = new CCNumberFragment();
        nameFragment = new CCNameFragment();
        validityFragment = new CCValidityFragment();
        secureCodeFragment = new CCSecureCodeFragment();
        adapter.addFragment(numberFragment);
        adapter.addFragment(nameFragment);
        adapter.addFragment(validityFragment);
        adapter.addFragment(secureCodeFragment);

        total_item = adapter.getCount() - 1;
        viewPager.setAdapter(adapter);

    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        // Flip to the back.
        //setCustomAnimations(int enter, int exit, int popEnter, int popExit)

        mShowingBack = true;

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardBackFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int pos = viewPager.getCurrentItem();
        if (pos > 0) {
            viewPager.setCurrentItem(pos - 1);
        } else
            super.onBackPressed();
    }

    public void nextClick() {
        btnNext.performClick();
    }

}
