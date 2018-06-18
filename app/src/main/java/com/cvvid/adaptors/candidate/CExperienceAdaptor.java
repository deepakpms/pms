package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvvid.R;
import com.cvvid.activities.candidate.EditCExprience;
import com.cvvid.common.HeaderViewHolder;
import com.cvvid.common.ItemViewHolder;
import com.cvvid.models.candidate.CEItemObject;

import java.util.List;

public class CExperienceAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CExperienceAdaptor.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<CEItemObject> itemObjects;
    private Context mContext;

    public CExperienceAdaptor(Context mContext, List<CEItemObject> itemObjects) {
        this.itemObjects = itemObjects;
        this.mContext = mContext;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new HeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cexperience_item_layout, parent, false);
            return new ItemViewHolder(layoutView);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CEItemObject mObject = itemObjects.get(position);
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).headerTitle.setText(mObject.getName());

            ((HeaderViewHolder) holder).headerTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext,EditCExprience.class);
                    intent.putExtra("category_id",mObject.getId());
                    intent.putExtra("category_name",mObject.getName());
                    mContext.startActivity(intent);

                }
            });


        }else if(holder instanceof ItemViewHolder){
            ((ItemViewHolder) holder).itemContent.setText(mObject.getName());
        }
    }
    private CEItemObject getItem(int position) {
        return itemObjects.get(position);
    }
    @Override
    public int getItemCount() {
        return itemObjects.size();
    }
    @Override
    public int getItemViewType(int position) {

        final CEItemObject mObject = itemObjects.get(position);

        if (isPositionHeader(mObject.getStatus()))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }
    private boolean isPositionHeader(boolean status) {
        return status;
    }
}