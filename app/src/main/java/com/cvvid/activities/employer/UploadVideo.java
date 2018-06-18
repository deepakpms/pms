package com.cvvid.activities.employer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.common.AndroidMultiPartEntity;
import com.cvvid.common.ImageFilePath;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.VideoFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_CREATE_VIDEO;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.EMPLOYER_CREATE_VIDEO;

public class UploadVideo extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String EMP_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String PROFILE_ID;

    String imagepath;
    File sourceFile;
    long totalSize = 0;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    private String filePath = null;
    private String isChoose = null;
    private String isRecord = null;
    Context context;

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "CVVid CV Videos";

    // LogCat tag
    private static final String TAG = VideoFragment.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private EditText input_vname;

    private Uri fileUri; // file url to store image/video

    public static final String FILE_UPLOAD_URL = CONFIG_URL+"/videoupload/profile_id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);


//        session
        session = new SessionManager(this);

        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        PROFILE_ID = user.get(SessionManager.PROFILE_ID);

        EMP_ID = user.get(SessionManager.EMPLOYER_ID);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        Button choose_video = (Button) findViewById(R.id.choose_video);
        Button record_video = (Button) findViewById(R.id.record_video);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);
        Button btn_upload = (Button) findViewById(R.id.btn_upload);
        input_vname = (EditText) findViewById(R.id.input_vname);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Boolean hasPermission = (ContextCompat.checkSelfPermission(UploadVideo.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(UploadVideo.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        isChoose = i.getStringExtra("isChoose");
        isRecord = i.getStringExtra("isRecord");

        if(filePath != null && !filePath.equals("")) {
            File f = new File(filePath);
            input_vname.setText(String.valueOf(f.getName()));
            input_vname.setEnabled(false);
        }

        if(isChoose != null && isChoose.equals("1"))
        {
            Drawable image=(Drawable)getResources().getDrawable(R.drawable.graystyle);
            record_video.setBackgroundDrawable(image);
            record_video.setEnabled(false);
//            record_video.setTextColor(Color.parseColor("#bdbdbd"));
        }

        if(isRecord != null && isRecord.equals("1"))
        {
            Drawable image=(Drawable)getResources().getDrawable(R.drawable.graystyle);
            choose_video.setBackgroundDrawable(image);
            choose_video.setEnabled(false);
//            record_video.setTextColor(Color.parseColor("#bdbdbd"));
        }


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
         * Choose video button click event
         */
        choose_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Choose video
                chooseVideo();
            }
        });

        /**
         * Record video button click event
         */
        record_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });

        /**
         * save video button click event
         */
        btn_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                if(filePath != null && !filePath.equals("")) {
                    // uploading the file to server
                    new UploadFileToServer().execute();

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Sorry! select video",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

    }

//    choose video
    private void chooseVideo()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*"); // intent.setType("video/*"); to select videos to upload
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, MY_INTENT_CLICK);
    }

//    record video
    private void recordVideo()
    {
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        } else {
            File file = new File(fileUri.getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");

        System.out.println("fileuriiiiiiiiiii"+fileUri);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(UploadVideo.this, "You must give access to storage.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == MY_INTENT_CLICK) {
            if (resultCode == RESULT_OK) {

                Uri selectedImageUri = data.getData();
                imagepath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);

                Intent i = new Intent(UploadVideo.this, UploadVideo.class);
                i.putExtra("filePath", imagepath);
                i.putExtra("isImage", false);
                i.putExtra("isChoose", "1");
                i.putExtra("isRecord", "0");
                startActivity(i);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(UploadVideo.this, UploadVideo.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        i.putExtra("isChoose", "0");
        i.putExtra("isRecord", "1");
        startActivity(i);
    }


    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }



    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(EMPLOYER_CREATE_VIDEO+EMP_ID);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("video", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("video_name", new StringBody(input_vname.getText().toString()));
//                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
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

                Toast.makeText(UploadVideo.this, "Bad internet",
                        Toast.LENGTH_SHORT).show();
                finish();

            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

                try{

                    JSONObject jObj = new JSONObject(result);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");

                    if(status == 200){
                        showAlert(message);
                    }else{
                        showAlert("Upload failed! please try again");
                    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //send fragment
                        sendFragment();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private  void sendFragment()
    {
        Intent intent=new Intent(getApplicationContext(),EmployerProfile.class);
        intent.putExtra("issetting","0");
        intent.putExtra("position","1");
        startActivity(intent);
    }



}
