package com.cvvid.fragments.candidate;


import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.BuildConfig;
import com.cvvid.common.PermissionCheck;
import com.cvvid.R;
import com.cvvid.activities.candidate.ViewBCard;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CBusinessCard extends Fragment {

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private TextView name_txt,email_txt,slug_txt;
    private String name,email,slug;
    private Button btn_save,btn_view;
    private View view;
    private String pdf_url="";
    ProgressDialog progressDialog;
    private Context context;

    public CBusinessCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_business_card, container, false);

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        context = getContext();

//        session
        session = new SessionManager(getContext());

        name_txt = (TextView) view.findViewById(R.id.name);
        email_txt = (TextView) view.findViewById(R.id.email);
        slug_txt = (TextView) view.findViewById(R.id.slug);

        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_view = (Button) view.findViewById(R.id.btn_view);
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
          //save btn
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(PermissionCheck.readAndWriteExternalStorage(context)){
                    downloadCard();
                }
            }
        });

        //save btn
        btn_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ViewBCard.class);
                i.putExtra("url", pdf_url);
                startActivity(i);
//                view(pdf_url);
            }
        });

        myWebServiceFun();

        haveStoragePermission();

        return view;
    }

    public void myWebServiceFun() {

        String url = "http://pdev.work/cvvidapi/api/viewBCard/id/"+USER_ID;

        getResponse(Request.Method.GET, url, null,
                new CBusinessCard.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {
                            JSONObject jarray = new JSONObject(result);
                             // do your work with response object

                            JSONArray jUser = jarray.getJSONArray("data");

                            JSONObject c = jUser.getJSONObject(0);

                            pdf_url = c.getString("url");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                Toast.makeText(getContext(), e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(getContext()).addToRequestQueue(strreq, cancel_req_tag);
    }

    public void downloadCard() {

        String url = pdf_url;
        String fileName = "businesscard";

        try {
            if (url != null && !url.isEmpty()) {
                Uri uri = Uri.parse(url);
                getContext().registerReceiver(attachmentDownloadCompleteReceive, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setMimeType(getMimeType(uri.toString()));
                request.setTitle(fileName);
                request.setDescription("Downloading attachment..");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager)getContext().getSystemService(getContext().DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "Please insert an SD card to download file", Toast.LENGTH_SHORT).show();
        }

    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    private void getLoadData(final String USER_ID, final View view)
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String url = CONFIG_URL+"/businesscard/id/" + USER_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    String name = c.getString("forenames") + c.getString("surname");
                    String email = c.getString("email") ;
                    String slug = c.getString("slug") ;

                    name_txt.setText(name);
                    email_txt.setText(email);
                    slug_txt.setText(slug);

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


    public void view(String purl)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse( "http://docs.google.com/viewer?url=" + purl), "text/html");

        startActivity(intent);
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

                attachmentUri = FileProvider.getUriForFile(getContext(),
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
                Toast.makeText(getContext(), "unable to open", Toast.LENGTH_LONG).show();
            }
        }
    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
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
