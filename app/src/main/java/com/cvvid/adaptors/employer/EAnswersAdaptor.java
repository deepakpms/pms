package com.cvvid.adaptors.employer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.activities.candidate.EditCExprience;
import com.cvvid.activities.employer.ViewEAnswers;
import com.cvvid.common.HeaderViewHolder;
import com.cvvid.common.ItemViewHolder;
import com.cvvid.models.candidate.CEItemObject;
import com.cvvid.models.employer.AnswersModel;

import java.util.List;

public class EAnswersAdaptor extends RecyclerView.Adapter<EAnswersAdaptor.MyViewHolder> {

    private Context mContext;
    private String jobid;
    private List<AnswersModel> albumList;

    public EAnswersAdaptor(Context mContext, List<AnswersModel> albumList, String jobid) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.jobid = jobid;
    }

    @Override
    public EAnswersAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answers_list, parent, false);

        return new EAnswersAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EAnswersAdaptor.MyViewHolder holder, int position) {
        final AnswersModel album = albumList.get(position);

        holder.itemName.setText(album.getUsername()+" - "+album.getQuestions());

        holder.view_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext, ViewEAnswers.class);
                i.putExtra("jobid", jobid);
                i.putExtra("videourl", album.getVideourl());
                i.putExtra("questions", album.getQuestions());
                mContext.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName;
        public ImageView view_answers;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.itemName);
            view_answers = (ImageView) itemView.findViewById(R.id.view_answers);

        }
    }
}