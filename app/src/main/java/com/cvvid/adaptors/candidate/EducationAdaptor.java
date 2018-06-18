package com.cvvid.adaptors.candidate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.activities.candidate.EditEducation;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.models.candidate.EducationModel;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EducationAdaptor extends RecyclerSwipeAdapter<EducationAdaptor.SimpleViewHolder> {

    private Context mContext;
    private ArrayList<EducationModel> educationList;
    ProgressDialog progressDialog;
    String URL_FOR_DELETE = CONFIG_URL+"/deleteeducation/id/";
    ViewGroup pp;
    Activity act;
    SimpleViewHolder viewHolders;
    public EducationAdaptor(Context context, ArrayList<EducationModel> objects) {
        this.mContext = context;
        this.educationList = objects;
        // Progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.education_items, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        final EducationModel item = educationList.get(position);
        viewHolders  = viewHolder;
        viewHolder.institution.setText(item.getInstitution());
        viewHolder.field.setText(item.getField());
        viewHolder.completion_date.setText(item.getCompletion_date());
        viewHolder.description.setText(item.getDescription());


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //dari kiri
//        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        //dari kanan
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));



        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " Click : " + item.getInstitution() + " \n" + item.getField(), Toast.LENGTH_SHORT).show();
            }
        });

//        viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Clicked on Information " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

//        viewHolder.Share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Toast.makeText(view.getContext(), "Clicked on Share " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext, EditEducation.class);
                i.putExtra("eduid", item.getId());
                mContext.startActivity(i);
            }
        });

        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.delete_video_popup, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                alertDialogBuilderUserInput.setView(mView);


                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                deleteEducation(item.getId(), position);

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

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView institution;
        public TextView field;
        public TextView completion_date;
        public TextView description;
        public TextView Delete;
        public TextView Edit;
//        public TextView Share;
//        public ImageButton btnLocation;
        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            institution = (TextView) itemView.findViewById(R.id.institution);
            field = (TextView) itemView.findViewById(R.id.field);
            completion_date = (TextView) itemView.findViewById(R.id.completion_date);
            description = (TextView) itemView.findViewById(R.id.description);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
        }
    }

    public void deleteEducation(final String id, final int position)
    {
        // Progress dialog
        String cancel_req_tag = "delete";
        progressDialog.setMessage("Please wait a moment");
        showDialog();
        String url = URL_FOR_DELETE + id;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    System.out.println("scc--"+jObj);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {

                        Toast.makeText(mContext,"Deleted successfully", Toast.LENGTH_LONG).show();
                        mItemManger.removeShownLayouts(viewHolders.swipeLayout);
                        educationList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, educationList.size());
                        mItemManger.closeAllItems();
                        //sendFragment();
                    }
                    else
                    {
                        Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(mContext).addToRequestQueue(strReq, cancel_req_tag);
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
