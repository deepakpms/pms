package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.activities.candidate.AddCSkills;
import com.cvvid.models.candidate.SkillsCategoryItem;

import java.util.List;

public class SCategoryAdaptor extends RecyclerView.Adapter<SCategoryAdaptor.MyViewHolder> {

    private Context mContext;
    private List<SkillsCategoryItem> albumList;

    public SCategoryAdaptor(Context mContext, List<SkillsCategoryItem> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public SCategoryAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.skillcategory_items, parent, false);

        return new SCategoryAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SCategoryAdaptor.MyViewHolder holder, int position) {
        final SkillsCategoryItem album = albumList.get(position);
        holder.skillcatname.setText(album.getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement onClick
                Intent intent = new Intent(mContext,AddCSkills.class);
                intent.putExtra("category_id",album.getId());
                intent.putExtra("category_name",album.getName());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView skillcatname;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            skillcatname = (TextView) itemView.findViewById(R.id.skillname);
        }
    }
}