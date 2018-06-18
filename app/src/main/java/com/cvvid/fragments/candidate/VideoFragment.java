package com.cvvid.fragments.candidate;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.activities.candidate.UploadVideo;
import com.cvvid.adaptors.candidate.VideoListAdaptor;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    RecyclerView recyclerview;
    ArrayList<VideoModel> videoList;

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String PROFILE_ID;

    ProgressDialog progressDialog;

    private Button addvideo;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerview = (RecyclerView) view.findViewById(R.id.videorecycler);



        addvideo = (Button)view.findViewById(R.id.btn_create);

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

        videoList = new ArrayList<>();

//        button action
        addvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() ,UploadVideo.class);
                startActivity(intent);
            }
        });

        getProfileVideos(PROFILE_ID);

        return view;

    }

    private void getProfileVideos(String profile_id){

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();
        //Creating a string request
        String URL_FOR_DETAIL = LOCATION_URL.PROFILE_VIDEOS + profile_id;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);
                    //  System.exit(0);
                    JSONArray jUser = jarray.getJSONArray("data");
                    String JSON_RESULT = jUser.toString();
                    VideoModel item;
                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new VideoModel(c.getString("id"), c.getString("name"), c.getString("duration")+" : 00", c.getString("photo"), c.getString("video"));
                        String isdef = "No";
                        if(c.getString("is_default").equals("1"))
                        {
                            isdef = "Yes";
                        }
                        item.setId(c.getString("id"));
                        item.setName(c.getString("name"));
                        item.setDuration(c.getString("duration")+" : 00");
                        item.setVideo(c.getString("video"));
                        item.setImage(c.getString("photo"));

//                        mVidAdapter.notifyDataSetChanged();
                        videoList.add(item);
                    }

                    VideoListAdaptor adapter = new VideoListAdaptor(getContext(), videoList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                    RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;

                    recyclerview.setLayoutManager(mLayoutManager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(adapter);

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

}
