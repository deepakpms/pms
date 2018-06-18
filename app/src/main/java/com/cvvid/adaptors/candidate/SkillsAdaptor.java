package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.models.candidate.SkillsItem;

import java.util.List;

public class SkillsAdaptor extends RecyclerView.Adapter<SkillsAdaptor.MyViewHolder> {

    private Context mContext;
    private List<SkillsItem> albumList;

    public SkillsAdaptor(Context mContext, List<SkillsItem> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public SkillsAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.skill_items, parent, false);

        return new SkillsAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SkillsAdaptor.MyViewHolder holder, int position) {
        SkillsItem album = albumList.get(position);
        holder.skillname.setText(album.getName());

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView skillname;
        public MyViewHolder(View itemView) {
            super(itemView);

            skillname = (TextView) itemView.findViewById(R.id.skillname);
        }
    }
}