package com.cvvid.fragments.candidate;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.activities.candidate.ImageLibraryCandidate;
import com.cvvid.activities.candidate.ProfileCompletion;
import com.cvvid.R;
import com.cvvid.common.ViewPagerAdapter;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.AndroidMultiPartEntity;
import com.cvvid.common.DownloadImageTask;
import com.cvvid.common.ImageFilePath;
import com.cvvid.common.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CandidateProfileFragment extends Fragment {

    String imagepath;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter madapter;
    private ImageView logout,profile_image;
    private Context c;
    private String fposition = null;

    private String USER_ID;
    private String USER_NAME;
    private String USER_TYPE;

    // Session Manager Class
    SessionManager session;

    ProgressDialog progressDialog;

    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final String IMAGE_UPLOAD = CONFIG_URL+"/insertMediaManager/id/";



    public CandidateProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        CandidateProfileFragment myFragment = new CandidateProfileFragment();
        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate_profile, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        logout = (ImageView) view.findViewById(R.id.logout);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);

        tabLayout.setupWithViewPager(viewPager);

        madapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        madapter.AddFragmentPage(new BasicDetailsFragment(), "Basic Details");
        madapter.AddFragmentPage(new VideoFragment(), "Videos");
        madapter.AddFragmentPage(new DocumentFragment(), "Supporting Document");
        madapter.AddFragmentPage(new CExperienceFragment(), "Career Experience");
        madapter.AddFragmentPage(new PExperienceFragment(), "Professional Experience");
        madapter.AddFragmentPage(new EducationFragment(), "Education");
        madapter.AddFragmentPage(new HobbiesFragment(), "Hobbies");
        madapter.AddFragmentPage(new LanguageFragment(), "Language");
        madapter.AddFragmentPage(new ProfileCompletion(), "Profile Completion");
        viewPager.setAdapter(madapter);


        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getContext(),R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        Bundle bundle = getActivity().getIntent().getExtras();

        if (bundle  != null) {
            fposition = bundle.getString("position");
            viewPager.setCurrentItem(Integer.parseInt(fposition));
        }else{
            viewPager.setCurrentItem(Integer.parseInt("0"));
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               /* switch (position)
                case 0:*/
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

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

        getLoadData(USER_ID, view);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//logout
                session.logoutUser();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeprofilephoto();
            }
        });

        Boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        return view;

    }


    public void changeprofilephoto()
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Change Photo");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_camera_enhance_black_24dp);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Choose form gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                fileChooser();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("CVVid Image Library", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed No button. Write Logic Here
                Intent i = new Intent(getContext(), ImageLibraryCandidate.class);
                startActivity(i);

            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    //    choose video
    private void fileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // intent.setType("video/*"); to select videos to upload
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, MY_INTENT_CLICK);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                     } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
//                Toast.makeText(getContext(),
//                        "User cancelled image capture", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to capture image
//                Toast.makeText(getContext(),
//                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
//                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

             } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
//                Toast.makeText(getContext(),
//                        "User cancelled video recording", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to record video
//                Toast.makeText(getContext(),
//                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
//                        .show();
            }
        } else if (requestCode == MY_INTENT_CLICK) {
            if (resultCode == RESULT_OK) {

                Uri selectedImageUri = data.getData();
                imagepath = ImageFilePath.getPath(getContext(), selectedImageUri);

                new UploadFileToServer(imagepath).execute();

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
//                Toast.makeText(getContext(),
//                        "User cancelled video recording", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to record video
//                Toast.makeText(getContext(),
//                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    }


    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        String imagePath;

        public UploadFileToServer(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(IMAGE_UPLOAD+USER_ID);


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });

                File sourceFile = new File(imagePath);

                // Adding file data to http body
                entity.addPart("media", new FileBody(sourceFile));
                entity.addPart("collection_name", new StringBody("photos"));
                entity.addPart("model_type", new StringBody("App\\\\Users\\\\Candidate"));
                entity.addPart("model_id", new StringBody(USER_ID));
                // Extra parameters if you want to pass to server

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (Throwable t){
                Log.e("OSFP.News",t.getMessage(),t);

                Toast.makeText(getContext(), "Bad internet",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();

            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from servers: " + result);

            try{

                JSONObject jObj = new JSONObject(result);

                int status = jObj.getInt("status");
                String message = jObj.getString("message");

                // showing the server response in an alert dialog
                showAlert(message);

            } catch (JSONException e) {
            e.printStackTrace();
            System.out.print(e);
        }


            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //send fragment
                        sendFragment();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void getLoadData(final String USER_ID, final View view) {

        String url = LOCATION_URL.CANDIDATE_DETAIL + USER_ID;

        getResponse(Request.Method.GET, url, null,
                new CandidateProfileFragment.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {
                            JSONObject jarray = new JSONObject(result);

                            JSONArray jUser = jarray.getJSONArray("data");

                            JSONObject c = jUser.getJSONObject(0);

                            String cover_id = c.getString("cover_id");
                            String cover = "";
                            if(cover_id.equals("")){
                                cover = c.getString("coverdefault");
                            }else{
                                cover = c.getString("cover");
                            }

                            String photo_id = c.getString("photo_id");
                            String photo = "";
                            if(photo_id.equals("")){
                                photo = c.getString("photodefault");
                            }else{
                                photo = c.getString("photo");
                            }


                            String stripe_active = c.getString("stripe_active");
                            String slug = c.getString("slug");
                            String upgrade = c.getString("upgrade");
                            String acctype="";
                            if(upgrade.equals("1")){
                                acctype = "Premium Account";
                            }else{
                                acctype = "Basic Account";
                            }

                            TextView candidatename = (TextView) view.findViewById(R.id.usertitle);
                            candidatename.setText(c.getString("forenames")+" "+c.getString("surname"));
                            TextView slugname = (TextView) view.findViewById(R.id.slugartist);
                            slugname.setText(acctype);

                            CircleImageView imageView = (CircleImageView) view.findViewById(R.id.profile_image);

                            new DownloadImageTask((ImageView)view.findViewById(R.id.profile_image))
                                    .execute(photo);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }



    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }


    public void getResponse(int method, String url, JSONObject jsonValue, final CandidateProfileFragment.VolleyCallback callback) {

        String cancel_req_tag = "cancel";

        RequestQueue queue = AppSingleton.getInstance(getContext()).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(getContext()).addToRequestQueue(strreq, cancel_req_tag);
    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getContext(),CandidateProfile.class);
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
