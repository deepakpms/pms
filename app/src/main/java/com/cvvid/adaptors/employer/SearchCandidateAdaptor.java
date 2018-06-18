package com.cvvid.adaptors.employer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.activities.candidate.JobDescription;
import com.cvvid.models.candidate.SearchItemJobList;
import com.cvvid.models.employer.SearchItemCandidateList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchCandidateAdaptor extends RecyclerView.Adapter<SearchCandidateAdaptor.MyViewHolder> {

    private Context mContext;
    private List<SearchItemCandidateList> albumList;
    private CardView cardView;

    public SearchCandidateAdaptor(Context mContext, List<SearchItemCandidateList> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public SearchCandidateAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchcandidate_list, parent, false);

        return new SearchCandidateAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchCandidateAdaptor.MyViewHolder holder, int position) {
        final SearchItemCandidateList album = albumList.get(position);
        holder.name.setText(album.getName());
        holder.job.setText(album.getJob());
        holder.location.setText(album.getLocation());
        if(!album.getImage().isEmpty()) {
            Picasso.with(mContext).load(album.getImage())
                    .fit()
                    .centerCrop()
                    .error(R.drawable.defaultprofile)
                    .into(holder.image);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement onClick
                Intent intent = new Intent(mContext,JobDescription.class);
                intent.putExtra("job_id",album.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, job, location,view_count;
        public ImageView image;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            name = (TextView) itemView.findViewById(R.id.name);
            job = (TextView) itemView.findViewById(R.id.job);
            location = (TextView) itemView.findViewById(R.id.location);
            image = (ImageView) itemView.findViewById(R.id.photo);
        }
    }
}