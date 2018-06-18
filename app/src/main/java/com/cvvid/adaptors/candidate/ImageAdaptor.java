package com.cvvid.adaptors.candidate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateProfile;
import com.cvvid.activities.candidate.ImageLibraryCandidate;
import com.cvvid.activities.candidate.ShowVideo;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.ImageListModel;
import com.cvvid.models.candidate.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ViewHolder> {

    private Context mContext;
    private List<ImageListModel> mList;
    SessionManager session;
    String PROFILE_ID;

    public ImageAdaptor(Context context, List<ImageListModel> list) {

        mContext = context;
        mList = list;
    }

    @Override
    public ImageAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.candidate_image_list, parent, false);

        // Return a new holder instance
        ImageAdaptor.ViewHolder viewHolder = new ImageAdaptor.ViewHolder(contactView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ImageAdaptor.ViewHolder holder, final int position) {

        final ImageListModel contact = mList.get(position);

        if (contact.getUrl().isEmpty()) {
            holder.thumbnail.setImageResource(R.drawable.defaultprofile);
        } else {

            Glide.with(mContext)
                    .load(contact.getUrl())
                    .centerCrop()
                    .error(R.drawable.defaultprofile)
                    .placeholder(R.drawable.defaultprofile)
                    .into(holder.thumbnail);

        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               setPicture(contact.getId());
            }
        });

    }

    public void setPicture(final String mediaid)
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("Set as profile picture?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_add_a_photo_black_24dp);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
              savePicture(mediaid);

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed No button. Write Logic Here
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    public void savePicture(final String mediaid)
    {
        //        session
        session = new SessionManager(mContext);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        PROFILE_ID = user.get(SessionManager.PROFILE_ID);

        String URL_FOR_LOGIN = LOCATION_URL.SAVE_PHOTO;

        StringRequest strReq = new StringRequest(
                Request.Method.POST,URL_FOR_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("jobj---"+response);
                        try {
                            JSONObject jObj = new JSONObject(response);


                            int status = jObj.getInt("status");
                            String message = jObj.getString("message");

                            if(status == 200){
                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(mContext, CandidateProfile.class);
                                mContext.startActivity(i);

                            }else{
                                Toast.makeText(mContext, "failed to set profile picture", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.print(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(mContext,
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mediaid", mediaid);
                params.put("profileid", PROFILE_ID);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppSingleton.getInstance(mContext).addToRequestQueue(strReq, "cancel");
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
//        public ImageView item_image;
        public ImageView thumbnail;
        public LinearLayout relativeLayout;
//        protected Button btn_action;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            relativeLayout = itemView.findViewById(R.id.image_lay);


        }


    }
}