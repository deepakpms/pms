package com.cvvid.adaptors.employer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.activities.employer.EQuestionsList;
import com.cvvid.activities.employer.EditOffice;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.models.employer.EmployerOfficeModel;
import com.cvvid.models.employer.QuestionsModel;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EQuestionsAdaptor extends RecyclerSwipeAdapter<EQuestionsAdaptor.SimpleViewHolder> {

    private Context mContext;
    private EditText questions;
    private ArrayList<QuestionsModel> documentList;
    ProgressDialog progressDialog;
    private String jobid;
    ViewGroup pp;
    Activity act;
    SimpleViewHolder viewHolders;
    String URL_FOR_DELETE = CONFIG_URL+"/deletequestions/id/";

    public EQuestionsAdaptor(Context context, ArrayList<QuestionsModel> objects, String jobid) {
        this.mContext = context;
        this.documentList = objects;
        this.jobid = jobid;
        // Progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equestions_items, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        final QuestionsModel item = documentList.get(position);
        viewHolders  = viewHolder;
        viewHolder.questions.setText(item.getQuestions());

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

//                Intent intent = new Intent(mContext,AddDocument.class);
//                intent.putExtra("doc_id",item.getId());
//                intent.putExtra("doc_name",contact.getName());
//                intent.putExtra("vid",contact.getId());
//                mContext.startActivity(intent);
              //  Toast.makeText(mContext, " Click : " + item.getLocation() + " \n" + item.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });

//        viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Clicked on Information " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.swiperight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.swipeLayout.open(true);
                //viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));
                //viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.Right);
               // SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper);
               // Toast.makeText(view.getContext(), "Clicked on Share " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // editPopup(item.getId());

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.custom_popup_questions, null);

                questions = (EditText)mView.findViewById(R.id.questions);
                Button btn_accept = (Button)mView.findViewById(R.id.btn_accept);

                questions.setText(item.getQuestions());

                btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateQuestion(item.getId());
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

//                Intent i = new Intent(mContext, EditOffice.class);
//                i.putExtra("quesid", item.getId());
//                mContext.startActivity(i);

              //  Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.location.getText().toString(), Toast.LENGTH_SHORT).show();
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
                                deleteQuestion(item.getId(), position);

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

    private void updateQuestion(final String id){

        String cancel_req_tag = "Cancel";
        progressDialog.setMessage("loading..");
        showDialog();

        //Creating a string request

        String url = CONFIG_URL+"/editquestions/id/"+id;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Status Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    int status = jObj.getInt("status");
                    String message = jObj.getString("message");
                    if(status == 200)
                    {
                        //dialog.dismiss();
                        Intent i = new Intent(mContext, EQuestionsList.class);
                        i.putExtra("jobid", jobid);
                        mContext.startActivity(i);

                        Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Questions Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("question", questions.getText().toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(mContext).addToRequestQueue(strReq, cancel_req_tag);

    }




    @Override
    public int getItemCount() {
        return documentList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView questions;
        public ImageView swiperight;
        public TextView Delete;
        public TextView Edit;
//        public TextView Share;
//        public ImageButton btnLocation;
        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            questions = (TextView) itemView.findViewById(R.id.questions);
            swiperight = (ImageView) itemView.findViewById(R.id.swiperight);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
        }
    }

    public void deleteQuestion(final String id, final int position)
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
                        documentList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, documentList.size());
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
