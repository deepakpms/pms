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
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class AddLanguage extends AppCompatActivity {

    String imagepath;
    File sourceFile;
    long totalSize = 0;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private String filePath = null;
    private String isFile = null;
    Context context;

    private JSONArray result;

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "CVVid Language";

    // LogCat tag
    private static final String TAG = AddLanguage.class.getSimpleName();
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

    EditText lang_field;
    MaterialSpinner prof_field;
    String lang,prof,USER_ID;
    // Session Manager Class
    SessionManager session;
    public static final String URL_SAVE_LANGUAGE = CONFIG_URL+"/createlanguage/id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_language);

        Button btn_save = (Button) findViewById(R.id.btn_save_language);
        displayname = (TextView) findViewById(R.id.displayname);

        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE) ;

        lang_field = (EditText)findViewById(R.id.language);
        prof_field = (MaterialSpinner) findViewById(R.id.proficiency);

//        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);

        validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);

        validation.addValidation(AddLanguage.this, R.id.language, RegexTemplate.NOT_EMPTY, R.string.language);


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
                    lang = lang_field.getText().toString();
                    prof = prof_field.getSelectedItem().toString();

                    saveLanguage(lang,prof);
                }

            }
        });


    }

    public void saveLanguage(final String lang, final  String prof )
    {
        String cancel_req_tag = "language";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        String url  = URL_SAVE_LANGUAGE + USER_ID;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "save hobby Response: " + response.toString());
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
                params.put("name", lang);
                params.put("proficiency", prof);
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
        intent.putExtra("issetting","0");
        intent.putExtra("position","7");
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
