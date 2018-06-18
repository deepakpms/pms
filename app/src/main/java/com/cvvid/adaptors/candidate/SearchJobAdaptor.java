package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvvid.activities.candidate.JobDescription;
import com.cvvid.R;
import com.cvvid.models.candidate.SearchItemJobList;

import java.util.List;

public class SearchJobAdaptor extends RecyclerView.Adapter<SearchJobAdaptor.MyViewHolder> {

    private Context mContext;
    private List<SearchItemJobList> albumList;
    private CardView cardView;

    public SearchJobAdaptor(Context mContext, List<SearchItemJobList> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public SearchJobAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchjob_list, parent, false);

        return new SearchJobAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchJobAdaptor.MyViewHolder holder, int position) {
        final SearchItemJobList album = albumList.get(position);
        holder.item_name.setText(album.getTitle());
        holder.salary.setText(album.getSalary());
        holder.location.setText(album.getLocation());

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
        public TextView item_name, salary, location, view_count;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            salary = (TextView) itemView.findViewById(R.id.salary);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }
}