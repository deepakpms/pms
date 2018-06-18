package com.cvvid.adaptors.employer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.models.candidate.CandidateFavouritesModel;
import com.cvvid.models.employer.ViewLikesModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewLikesAdapter extends RecyclerView.Adapter<ViewLikesAdapter.MyViewHolder> {

    private Context mContext;
    private List<ViewLikesModel> albumList;

    public ViewLikesAdapter(Context mContext, List<ViewLikesModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public ViewLikesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_fav_list, parent, false);

        return new ViewLikesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewLikesAdapter.MyViewHolder holder, int position) {
        ViewLikesModel album = albumList.get(position);
        holder.slugurl.setText(album.getSlug());

        if(!album.getPhoto().isEmpty()) {
            Picasso.with(mContext).load(album.getPhoto())
                    .fit()
                    .centerCrop()
                    .error(R.drawable.defaultprofile)
                    .into(holder.thumbnail);
        }


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView slugurl;
        public ImageView thumbnail;
        public MyViewHolder(View itemView) {
            super(itemView);

            slugurl = (TextView) itemView.findViewById(R.id.slugurl);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }
}