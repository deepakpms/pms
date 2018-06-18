package com.cvvid.fragments.employer;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.activities.employer.EmployerProfile;
import com.cvvid.activities.employer.UploadVideo;
import com.cvvid.adaptors.candidate.VideoListAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    RecyclerView recyclerview;
    ArrayList<VideoModel> videoList;

    private static final String TAG = "ShowVideo";

    // Session Manager Class
    SessionManager session;

    private String USER_ID,EMP_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String PROFILE_ID;

    ProgressDialog progressDialog;

    private Button addvideo,play,delete;
    private LinearLayout buttonPanel;
    private VideoView video;
    private TextView empty_view;

    private static final String URL_FOR_DELETE = LOCATION_URL.EMPLOEYR_VIDEO_DELETE;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emp_show_video, container, false);

        addvideo = (Button)view.findViewById(R.id.btn_create);
        play = (Button)view.findViewById(R.id.play);
        delete = (Button)view.findViewById(R.id.delete);
        buttonPanel = (LinearLayout)view.findViewById(R.id.buttonPanel);
        video = (VideoView)view.findViewById(R.id.emp_video_view);
        empty_view = (TextView)view.findViewById(R.id.empty_view);

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

        PROFILE_ID = user.get(SessionManager.PROFILE_ID);

        EMP_ID = user.get(SessionManager.EMPLOYER_ID);

        videoList = new ArrayList<>();

//        button action
        addvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,UploadVideo.class);
                startActivity(intent);
            }
        });
//        play vutton
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
            }
        });

        //        delete vutton
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.delete_video_popup, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
                alertDialogBuilderUserInput.setView(mView);


                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
//                                String name = userInputDialogEditText.getText().toString();
                                deleteVideo(EMP_ID);

                            }
                        })

                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });

        getProfileVideos(EMP_ID);

        return view;

    }

    private void deleteVideo(final String EMP_ID)
    {

        String cancel_req_tag = "register";
        progressDialog.setMessage("Please wait a moment");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_DELETE+EMP_ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    System.out.println("scc--"+jObj);

                    int status = jObj.getInt("status");
                    String message = "Deleted Successfully";
                    if(status == 200)
                    {

                        Toast.makeText(getContext(),message, Toast.LENGTH_LONG).show();
                        sendFragment();
                    }
                    else
                    {
                        Toast.makeText(getContext(),message, Toast.LENGTH_LONG).show();
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
                params.put("id", EMP_ID);
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }



    private void getProfileVideos(String emp_id){

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();
        //Creating a string request
        String URL_FOR_DETAIL = LOCATION_URL.EMPLOYER_GET_VIDEO + emp_id;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    int status = jarray.getInt("status");
                    String message = jarray.getString("message");

                    String data = jarray.getString("data");

                    if(status == 200)
                    {

                        String path=data;

                        Uri uri=Uri.parse(data);

                        video.setVideoURI(uri);
                        video.start();
                        addvideo.setVisibility(View.GONE);

                    }else{
                        video.setVisibility(View.GONE);
                        buttonPanel.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public class StretchVideoView extends VideoView {
        public StretchVideoView(Context context) {
            super(context);
        }

        public StretchVideoView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public StretchVideoView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private  void sendFragment()
    {
        Intent intent=new Intent(getContext(),EmployerProfile.class);
        intent.putExtra("issetting","0");
        intent.putExtra("position","1");
        startActivity(intent);
    }




}
