package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.models.candidate.ProfilesViewsModel;

import java.util.List;

public class ProfilesViewsAdapter extends RecyclerView.Adapter<ProfilesViewsAdapter.MyViewHolder> {

    private Context mContext;
    private List<ProfilesViewsModel> albumList;

    public ProfilesViewsAdapter(Context mContext, List<ProfilesViewsModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public ProfilesViewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_views_list, parent, false);

        return new ProfilesViewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfilesViewsAdapter.MyViewHolder holder, int position) {
        ProfilesViewsModel album = albumList.get(position);
        holder.username.setText(album.getForenames()+ " "+album.getSurname());
        holder.current_job.setText(album.getCurrent_job());
        holder.location.setText(album.getLocation());
        holder.view_count.setText(album.getView_count());

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView username, current_job, location, view_count;
        public MyViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.username);
            current_job = (TextView) itemView.findViewById(R.id.current_job);
            location = (TextView) itemView.findViewById(R.id.location);
            view_count = (TextView) itemView.findViewById(R.id.view_count);
        }
    }
}