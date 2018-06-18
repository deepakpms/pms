package com.cvvid.activities.candidate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.common.AndroidMultiPartEntity;
import com.cvvid.common.ImageFilePath;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;

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
import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EditDocument extends AppCompatActivity {

    private ProgressBar progressBar;
    Context context;
    long totalSize = 0;
    String imagepath;
    String docid;
    private TextView txtPercentage;
    private EditText name;
    private String filePath = null;
    private String isFile = null;
    private JSONArray result;
    private ArrayList<String> categoryarray;
    MaterialSpinner scategory;
    String filename;
    private ArrayList<String> publisharray;
    MaterialSpinner spublish;

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "CVVid Document";

    // LogCat tag
    private static final String TAG = EditDocument.class.getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int MY_INTENT_CLICK=302;

    private TextView displayname;

    ProgressDialog progressDialog;

    public static final String GET_DOC_URL = "http://pdev.work/cvvidapi/api/getprofiledocumentation/id/";
    public static final String UPDATE_DOC_URL = CONFIG_URL+"/updateuserdocumentation/id/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        Button choose_file = (Button) findViewById(R.id.choose_file);
        Button btn_upload = (Button) findViewById(R.id.btn_upload);
        displayname = (TextView) findViewById(R.id.displayname);
        name = (EditText)findViewById(R.id.input_name);
        ImageView back_btn = (ImageView)findViewById(R.id.close_activity);

        TextView headertxt = (TextView)findViewById(R.id.headertxt);
        headertxt.setText("Edit Documentation");

        categoryarray = new ArrayList<String>();
        publisharray = new ArrayList<String>();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        scategory =(MaterialSpinner)findViewById(R.id.reg_category);
        spublish =(MaterialSpinner)findViewById(R.id.reg_published);

        Boolean hasPermission = (ContextCompat.checkSelfPermission(EditDocument.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(EditDocument.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        Intent ie = getIntent();
        docid = ie.getStringExtra("docid");
        setData(docid);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        isFile = i.getStringExtra("isFile");
        System.out.println("FILEE=="+filePath);
        if(filePath != null && !filePath.equals("")) {
            File f = new File(filePath);
            displayname.setText(String.valueOf(f.getName()));
            displayname.setVisibility(View.VISIBLE);
        }

        /**
         * Choose video button click event
         */
        choose_file.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Choose video
                chooseFile();
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

        /**
         * save video button click event
         */
        btn_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // record video
                if(filename != null && !filename.equals("")) {
                    // uploading the file to server
                    new EditDocument.UploadFileToServer().execute();

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Sorry! select video",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setData(final String docid)
    {

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = CONFIG_URL+"/getprofiledocumentation/id/" + docid;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    TextView input_name = (TextView)findViewById(R.id.input_name);
                    input_name.setText(c.getString("name"));
                    String category = c.getString("category");
                    String published = c.getString("published");
                     filename = c.getString("filename");

                    if(category.equalsIgnoreCase("Certificates"))
                        scategory.setSelection(0);
                    if(category.equalsIgnoreCase("Qualifications"))
                        scategory.setSelection(1);
                    if(category.equalsIgnoreCase("Training skills/courses"))
                        scategory.setSelection(2);
                    if(category.equalsIgnoreCase("References"))
                        scategory.setSelection(3);

                    if(published.equalsIgnoreCase("Unpublished"))
                        spublish.setSelection(0);
                    if(published.equalsIgnoreCase("Published"))
                        spublish.setSelection(1);

                    TextView filenamet = (TextView)findViewById(R.id.displayname);
                    filenamet.setText(filename);
                    filenamet.setVisibility(View.VISIBLE);



                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(EditDocument.this,error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(this).addToRequestQueue(strReq, cancel_req_tag);
    }

    //    choose File
    private void chooseFile()
    {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
//        intent.setType("file/*"); // intent.setType("video/*"); to select videos to upload
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, MY_INTENT_CLICK);
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

                Intent i = new Intent(EditDocument.this, AddDocument.class);
                i.putExtra("filePath", imagepath);
                i.putExtra("isFile", "1");
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
        Intent i = new Intent(EditDocument.this, AddDocument.class);
        i.putExtra("filePath", imagepath);
        i.putExtra("isFile", "1");
        startActivity(i);
    }



    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
           // progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            //progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(UPDATE_DOC_URL+docid);
            System.out.println("FILEPATHH=="+filePath);

            int cat = 1;
            if(scategory.getSelectedItem().toString().equalsIgnoreCase("Certificates"))
                cat = 1;
            if(scategory.getSelectedItem().toString().equalsIgnoreCase("Qualifications"))
                cat = 2;
            if(scategory.getSelectedItem().toString().equalsIgnoreCase("Training skills/courses"))
                cat = 3;
            if(scategory.getSelectedItem().toString().equalsIgnoreCase("References"))
                cat = 4;

            int pub = 0;
            if(spublish.getSelectedItem().toString().equalsIgnoreCase("Unpublished"))
                pub = 0;
            if(spublish.getSelectedItem().toString().equalsIgnoreCase("Published"))
                pub = 1;


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                if(filePath != null) {

                    File sourceFile = new File(filePath);

                    // Adding file data to http body
                    entity.addPart("doc_file", new FileBody(sourceFile));

                 }
                entity.addPart("name", new StringBody(name.getText().toString()));
                entity.addPart("category_id", new StringBody(String.valueOf(cat)));
                entity.addPart("published", new StringBody(String.valueOf(pub)));
//                entity.addPart("email", new StringBody("abc@gmail.com"));

               // totalSize = entity.getContentLength();
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

                Toast.makeText(EditDocument.this, "Bad internet",
                        Toast.LENGTH_SHORT).show();
                finish();

            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert("Updated Successfully");

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
        Intent intent=new Intent(getApplicationContext(),CandidateProfile.class);
        intent.putExtra("position","2");
        intent.putExtra("issetting","0");
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
