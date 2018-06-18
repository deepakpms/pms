package com.cvvid.activities.employer;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.BuildConfig;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.BottomNavigationViewHelper;
import com.cvvid.common.PermissionCheck;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CandidateMessageFragment;
import com.cvvid.fragments.candidate.CandidateProfileFragment;
import com.cvvid.fragments.candidate.CandidateSettingFragment;
import com.cvvid.fragments.candidate.JobSearchFragment;
import com.cvvid.fragments.employer.CandidateSearchFragment;
import com.cvvid.fragments.employer.EmployerMessageFragment;
import com.cvvid.fragments.employer.EmployerProfileFragment;
import com.cvvid.fragments.employer.EmployerSettingFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EmployerProfile extends AppCompatActivity {

    private FrameLayout mMainFrame;

    private FragmentManager fragmentManager;

    private static final String TAG_FRAGMENT_ONE = "fragment_one";
    private static final String TAG_FRAGMENT_TWO = "fragment_two";
    private static final String TAG_FRAGMENT_THREE = "fragment_three";
    private static final String TAG_FRAGMENT_FOUR = "fragment_four";
    private static final String TAG_FRAGMENT_FIVE = "fragment_five";
    private Fragment currentFragment;
    private Context context;
    EmployerProfile activity;
    SessionManager session;
    String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        context = this;

        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);//Dont forgot this line

        // instantiate the fragment manager
        fragmentManager = getSupportFragmentManager();

//        currentFragment = CandidateProfileFragment.newInstance();

        Bundle bundle = getIntent().getExtras();

        if (bundle  != null) {

            if(bundle.getString("issetting").equals("1")) {

                navigation.setSelectedItemId(R.id.action_settings);

                Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FOUR);
                if (fragment == null) {
                    fragment = EmployerSettingFragment.newInstance();
                }
                replaceFragment(fragment, TAG_FRAGMENT_FOUR);

            }
            else {
                Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                if (fragment == null) {
                    fragment = EmployerProfileFragment.newInstance();
                }
                replaceFragment(fragment, TAG_FRAGMENT_ONE);
            }
        }
        else {
            Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
            if (fragment == null) {
                fragment = EmployerProfileFragment.newInstance();
            }
            replaceFragment(fragment, TAG_FRAGMENT_ONE);
        }

//        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);


        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                removeintent();
                                // I'm aware that this code can be optimized by a method which accepts a class definition and returns the proper fragment
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                                if (fragment == null) {
                                    fragment = EmployerProfileFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_ONE);

                                return true;
                            case R.id.action_message:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                                if (fragment == null) {
                                    fragment = EmployerMessageFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_TWO);

                                return true;
                            case R.id.action_jobsearch:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_THREE);
                                if (fragment == null) {
                                    fragment = CandidateSearchFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_THREE);

                                return true;
                            case R.id.action_settings:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FOUR);
                                if (fragment == null) {
                                    fragment = EmployerSettingFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_FOUR);

                                return true;

                            case R.id.action_jobs:
                                removeintent();
//                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FIVE);
//                                if (fragment == null) {
//                                    fragment = EmployerSettingFragment.newInstance();
//                                }
//                                replaceFragment(fragment, TAG_FRAGMENT_FIVE);

                                Intent i = new Intent(EmployerProfile.this, PostedJobs.class);
                                startActivity(i);

                                return true;

                        }
                        return true;
                    }
                });
    }

    private void removeintent()
    {
        getIntent().removeExtra("position");
        getIntent().removeExtra("issetting");
    }

    private void replaceFragment(@NonNull Fragment fragment, @NonNull String tag) {

        if (!fragment.equals(currentFragment)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_frame, fragment, tag)
                    .commit();
            currentFragment = fragment;
        }else {
            System.out.println("not dis");
        }
    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }


    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    public void getResponse(int method, String url, JSONObject jsonValue, final VolleyCallback callback) {

        String cancel_req_tag = CONFIG_URL+"/viewBCard/id/"+USER_ID;

        RequestQueue queue = AppSingleton.getInstance(context).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(EmployerProfile.this, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(EmployerProfile.this).addToRequestQueue(strreq, cancel_req_tag);
    }

    /**
     * Used to get MimeType from url.
     *
     * @param url Url.
     * @return Mime Type for the given url.
     */
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Attachment download complete receiver.
     * <p/>
     * 1. Receiver gets called once attachment download completed.
     * 2. Open the downloaded file.
     */
    BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                openDownloadedAttachment(context, downloadId);
            }
        }
    };


    /**
     * Used to open the downloaded attachment.
     *
     * @param context    Content.
     * @param downloadId Id of the downloaded file to open.
     */
    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
            }
        }
        cursor.close();
    }


    /**
     * Used to open the downloaded attachment.
     * <p/>
     * 1. Fire intent to open download file using external application.
     *
     * 2. Note:
     * 2.a. We can't share fileUri directly to other application (because we will get FileUriExposedException from Android7.0).
     * 2.b. Hence we can only share content uri with other application.
     * 2.c. We must have declared FileProvider in manifest.
     * 2.c. Refer - https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     *
     * @param context            Context.
     * @param attachmentUri      Uri of the downloaded attachment to be opened.
     * @param attachmentMimeType MimeType of the downloaded attachment.
     */
    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if(attachmentUri!=null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());

                attachmentUri = FileProvider.getUriForFile(EmployerProfile.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            openAttachmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(EmployerProfile.this, "unable to open", Toast.LENGTH_LONG).show();
            }
        }
    }

}
