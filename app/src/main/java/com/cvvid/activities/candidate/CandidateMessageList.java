package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.adaptors.candidate.CChatArrayAdapter;
import com.cvvid.common.DownloadImageTask;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.ChatMessageCandidate;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;
import static com.cvvid.apicall.LOCATION_URL.SAVE_MES;

public class CandidateMessageList extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private CChatArrayAdapter chatArrayAdapter;
    private List<ChatMessageCandidate> chatList;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String CON_ID;
    private String msg_to;
    private String USER_TYPE;
    private String USER_NAME;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_message_list);

        buttonSend = (Button) findViewById(R.id.button_chat_send);

//        listView = (ListView) findViewById(R.id.msgview);

        chatList = new ArrayList<>();

        //        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        Intent ie = getIntent();
        CON_ID = ie.getStringExtra("conversation_id");
        msg_to = ie.getStringExtra("msg_to");

        mRecyclerView = (RecyclerView) findViewById(R.id.reycler_chat);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        chatArrayAdapter =  new CChatArrayAdapter(this, chatList,USER_ID,msg_to, CON_ID);
        mRecyclerView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.edittext_chat);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == chatArrayAdapter.getItemCount() - 1) {
                  //  chatArrayAdapter.loadPreviousMessages();
                }
            }
        });

        loadheader();

    }

    void loadheader() {

        String url = CONFIG_URL+"/getuserdetails/id/"+msg_to;

        getResponse(Request.Method.GET, url, null,
                new CandidateMessageList.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {

                            JSONObject jarray = new JSONObject(result);

                            JSONArray jUser = jarray.getJSONArray("data");
                            ChatMessageCandidate item;

                            for (int i = 0; i < jUser.length(); i++)
                            {
                                JSONObject c = jUser.getJSONObject(i);

                                String photo_id = c.getString("photo_id");
                                String photo = "";
                                if(photo.equals("")){
                                    photo = c.getString("photodefault");
                                }else{
                                    photo = c.getString("photo");
                                }
                              //  CircleImageView imageView = (CircleImageView)findViewById(R.id.profile_image);

                                new DownloadImageTask((ImageView)findViewById(R.id.profile_image))
                                        .execute(photo);

//                                Picasso.with(CandidateMessageList.this).load(photo)
//                                        .fit()
//                                        .centerCrop()
//                                        .placeholder(R.drawable.profile).error(R.drawable.profile)
//                                        .into(imageView);

                                TextView candidatename = (TextView) findViewById(R.id.usertitle);
                                candidatename.setText(c.getString("forenames")+" "+c.getString("surname"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void getResponse(int method, String url, JSONObject jsonValue, final CandidateMessageList.VolleyCallback callback) {

        String cancel_req_tag = "Cancel";

        RequestQueue queue = AppSingleton.getInstance(this).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(CandidateMessageList.this, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(this).addToRequestQueue(strreq, cancel_req_tag);
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    private void sendChatMessage() {

        String conversation_id = CON_ID;
        String user_id = USER_ID;
        String msgto = msg_to;
        String message = chatText.getText().toString();

        sendMessage(user_id, conversation_id, msgto,message);

    }


    private void sendMessage(final String user_id, final String conversation_id,final String msgto, final String message)
    {
        new saveMessage(user_id, conversation_id,msgto,message).execute();
        chatText.setText("");
    }

    public class saveMessage extends AsyncTask<String, Void, String> {

        String user_id,conversation_id,msgto,message;
        ProgressDialog pd;


        public saveMessage(String user_id, String conversation_id,String msgto,String message) {
            this.user_id = user_id;
            this.conversation_id = conversation_id;
            this.msgto = msgto;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CandidateMessageList.this);
            pd.setMessage("Working ...");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    postData(user_id, conversation_id, msgto, message);
                }
            }.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result",s);
            pd.dismiss(); }
    }

    public void postData(final String user_id, final String conversation_id, final String msgto, final String message) {
        // Create a new HttpClient and Post Header

        String cancel_req_tag = "register";

        String saveurl = SAVE_MES + conversation_id;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                saveurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);

                    JSONArray jUser = jObj.getJSONArray("data");
                    ChatMessageCandidate item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new ChatMessageCandidate();

                        item.setId(c.getString("id"));
                        item.setConversation_id(c.getString("conversation_id"));
                        item.setUser_id(c.getString("user_id"));
                        item.setName(c.getString("name"));
                        item.setMessage(c.getString("message"));
                        item.setCreated_at(c.getString("created_at"));
                        chatList.add(0, new ChatMessageCandidate(c.getString("id"),c.getString("conversation_id"),c.getString("user_id"),c.getString("name"),c.getString("message"),c.getString("created_at")));
                    }
                    chatArrayAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("msg_from", user_id);
                params.put("msg_to", msgto);
                params.put("message", message);
                //   System.out.println("params--"+params.toString());
                return params;


            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }



}
