package com.cvvid.adaptors.employer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateMessageList;
import com.cvvid.activities.employer.EmployerMessageList;
import com.cvvid.models.candidate.CandidateMessageUserList;
import com.cvvid.models.employer.EmployerMessageUserList;

import java.util.List;

public class EmployerMessageUserAdaptor extends RecyclerView.Adapter<EmployerMessageUserAdaptor.MyViewHolder> {

    private Context mContext;
    private List<EmployerMessageUserList> albumList;
    private CardView cardView;

    public EmployerMessageUserAdaptor(Context mContext, List<EmployerMessageUserList> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public EmployerMessageUserAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_message_list, parent, false);

        return new EmployerMessageUserAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EmployerMessageUserAdaptor.MyViewHolder holder, int position) {
        final EmployerMessageUserList album = albumList.get(position);

        holder.message_name.setText(album.getName());
        holder.subject.setText(album.getSubject());
        holder.message.setText(album.getMessage());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement onClick
                Intent intent = new Intent(mContext,EmployerMessageList.class);
                intent.putExtra("conversation_id",album.getConversation_id());
                intent.putExtra("msg_to",album.getUser_id());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView message_name, subject, message;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            subject = (TextView) itemView.findViewById(R.id.subject);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}