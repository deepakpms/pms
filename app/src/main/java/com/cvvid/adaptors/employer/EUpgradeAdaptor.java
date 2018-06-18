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
import com.cvvid.activities.candidate.CandidateUpgrade;
import com.cvvid.activities.candidate.CheckoutActivity;
import com.cvvid.activities.employer.EmployerUpgrade;

import java.util.List;

public class EUpgradeAdaptor extends RecyclerView.Adapter<EUpgradeAdaptor.MyViewHolder> {

    private Context mContext;
    private List<EmployerUpgrade.SimplePlan> albumList;

    public EUpgradeAdaptor(Context mContext, List<EmployerUpgrade.SimplePlan> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public EUpgradeAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cupgrade_list, parent, false);

        return new EUpgradeAdaptor.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EUpgradeAdaptor.MyViewHolder holder, int position) {
        final EmployerUpgrade.SimplePlan album = albumList.get(position);
        holder.supheader.setText(album.getName());
        holder.item_price.setText(album.getAmount()+" "+album.getPrice_suffix());
        holder.billedtxt.setText(album.getDescription());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyIntent = new Intent(mContext,CheckoutActivity.class);
                buyIntent.putExtra("type", "employer");
                buyIntent.putExtra("plan_price",album.getAmount());
                buyIntent.putExtra("plan_name",""+album.getName());
                buyIntent.putExtra("plan_id",""+album.getId());
                buyIntent.putExtra("price_suffix",album.getPrice_suffix());
                buyIntent.putExtra("description",album.getDescription());
                mContext.startActivity(buyIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView supheader, item_price, billedtxt;
        public CardView card_view;
        public MyViewHolder(View itemView) {
            super(itemView);

            card_view = (CardView) itemView.findViewById(R.id.card_view);
            supheader = (TextView) itemView.findViewById(R.id.supheader);
            item_price = (TextView) itemView.findViewById(R.id.item_price);
            billedtxt = (TextView) itemView.findViewById(R.id.billedtxt);
        }
    }
}