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
import com.cvvid.activities.candidate.EditPExperience;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.models.candidate.PExperienceModel;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class PExperienceAdaptor extends RecyclerSwipeAdapter<PExperienceAdaptor.SimpleViewHolder> {

    private Context mContext;
    private ArrayList<PExperienceModel> pexeprenceList;
    ProgressDialog progressDialog;
    String URL_FOR_DELETE = CONFIG_URL+"/deleteprofessional/id/";
    ViewGroup pp;
    Activity act;
    SimpleViewHolder viewHolders;
    public PExperienceAdaptor(Context context, ArrayList<PExperienceModel> objects) {
        this.mContext = context;
        this.pexeprenceList = objects;
        // Progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }


    @Override
    public PExperienceAdaptor.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        pp = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pexperiences_items, parent, false);
        return new PExperienceAdaptor.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PExperienceAdaptor.SimpleViewHolder viewHolder, final int position) {

        final PExperienceModel item = pexeprenceList.get(position);
        viewHolders  = viewHolder;
        viewHolder.Name.setText(item.getCompany_name()+ " ( "+item.getLocation()+ " )");
        viewHolder.position.setText(item.getPosition());
        viewHolder.start_date.setText(item.getStart_date());
        viewHolder.end_date.setText(item.getEnd_date());
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
                Toast.makeText(mContext, " Click : " + item.getCompany_name() + " \n" + item.getLocation(), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext, EditPExperience.class);
                i.putExtra("pexpid", item.getId());
                mContext.startActivity(i);

                //Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
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
                                deletePExperience(item.getId(), position);

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
               // Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return pexeprenceList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView Name;
        public TextView position;
        public TextView start_date;
        public TextView end_date;
        public TextView description;
        public TextView Delete;
        public TextView Edit;
        //        public TextView Share;
//        public ImageButton btnLocation;
        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            Name = (TextView) itemView.findViewById(R.id.company_name);
            position = (TextView) itemView.findViewById(R.id.position);
            start_date = (TextView) itemView.findViewById(R.id.start_date);
            end_date = (TextView) itemView.findViewById(R.id.end_date);
            description = (TextView) itemView.findViewById(R.id.description);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
        }
    }


    public void deletePExperience(final String id, final int position)
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
                        pexeprenceList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, pexeprenceList.size());
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
