package com.cvvid.adaptors.employer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.models.candidate.ChatMessageCandidate;
import com.cvvid.models.employer.ChatMessageEmployer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class EChatArrayAdapter extends RecyclerView.Adapter<EChatArrayAdapter.MyViewHolder> {

    private List<ChatMessageEmployer> chatMessageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private String user_id,conversation_id,msg_to;
    private CircleImageView imageView;
    private TextView candidatename;

    public EChatArrayAdapter(Context mContext, List<ChatMessageEmployer> chatMessageList, String user_id, String msg_to, String conversation_id) {
        this.mContext = mContext;
        this.chatMessageList = chatMessageList;
        this.user_id = user_id;
        this.msg_to = msg_to;
        this.conversation_id = conversation_id;
        refresh();
    }

    // Retrieves 30 most recent messages.
    void refresh() {

        String url = CONFIG_URL+"/getmessage/id/"+conversation_id;

        getResponse(Request.Method.GET, url, null,
                new EChatArrayAdapter.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {

                            JSONObject jarray = new JSONObject(result);

                            JSONArray jUser = jarray.getJSONArray("data");
                            ChatMessageEmployer item;

                            for (int i = 0; i < jUser.length(); i++)
                            {
                                JSONObject c = jUser.getJSONObject(i);

                                item = new ChatMessageEmployer();

                                item.setId(c.getString("id"));
                                item.setConversation_id(c.getString("conversation_id"));
                                item.setUser_id(c.getString("user_id"));
                                item.setName(c.getString("name"));
                                item.setMessage(c.getString("message"));
                                item.setCreated_at(c.getString("created_at"));
                                chatMessageList.add(new ChatMessageEmployer(c.getString("id"),c.getString("conversation_id"),c.getString("user_id"),c.getString("name"),c.getString("message"),c.getString("created_at")));
                            }

                            notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void getResponse(int method, String url, JSONObject jsonValue, final EChatArrayAdapter.VolleyCallback callback) {

        String cancel_req_tag = "Cancel";

        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(mContext, e + "error", Toast.LENGTH_LONG).show();
            }
        });

        AppSingleton.getInstance(mContext).addToRequestQueue(strreq, cancel_req_tag);
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    @Override
    public EChatArrayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == VIEW_TYPE_MESSAGE_SENT)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.can_item_message_sent, parent, false);
            return new EChatArrayAdapter.MyViewHolder(itemView);
        }else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.can_item_message_received, parent, false);
            return new EChatArrayAdapter.MyViewHolder(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final EChatArrayAdapter.MyViewHolder holder, int position) {
        final ChatMessageEmployer album = chatMessageList.get(position);
        holder.chatName.setText(album.getName());
        holder.chatText.setText(album.getMessage());
        holder.chatTime.setText(timeage(album.getCreated_at()));
        System.out.println("position--"+position+"--"+user_id+"----"+msg_to);
//        if(msg_to.equals(album.getUser_id()))
//        {
//            Drawable image=(Drawable)mContext.getResources().getDrawable(R.drawable.rounded_rectangle_green);
//            holder.chatText.setBackground(image);
//        }
//        else {
//            Drawable image=(Drawable)mContext.getResources().getDrawable(R.drawable.rounded_rectangle_orange);
//            holder.chatText.setBackground(image);
//        }
//        if()
//        holder.card_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent buyIntent = new Intent(mContext,CandidatePayActivity.class);
//                buyIntent.putExtra("plan_price",album.getAmount());
//                buyIntent.putExtra("plan_name",""+album.getName());
//                buyIntent.putExtra("plan_id",""+album.getId());
//                buyIntent.putExtra("price_suffix",album.getPrice_suffix());
//                buyIntent.putExtra("description",album.getDescription());
//                mContext.startActivity(buyIntent);
//            }
//        });

    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        final ChatMessageEmployer album = chatMessageList.get(position);

        if (msg_to.equals(album.getUser_id())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView chatText,chatName,chatTime,candidatename;
        public  CircleImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);

            chatName = (TextView) itemView.findViewById(R.id.text_message_name);
            chatText = (TextView) itemView.findViewById(R.id.text_message_body);
            chatTime = (TextView) itemView.findViewById(R.id.text_message_time);
            imageView = (CircleImageView) itemView.findViewById(R.id.profile_image);
            candidatename = (TextView) itemView.findViewById(R.id.usertitle);
        }
    }

    public String timeage(String date)
    {
        String timeage = "";
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date past = format.parse(date);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
            {
                timeage = seconds+" seconds ago";
            }
            else if(minutes<60)
            {
                timeage = minutes+" minutes ago";
            }
            else if(hours<24)
            {
                timeage = hours+" hours ago";
            }
            else
            {
                timeage = days+" days ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }

        return timeage;
    }
}