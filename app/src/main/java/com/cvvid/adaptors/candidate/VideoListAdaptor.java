package com.cvvid.adaptors.candidate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvvid.R;
import com.cvvid.activities.candidate.ShowVideo;
import com.cvvid.models.candidate.VideoModel;

import java.util.ArrayList;

public class VideoListAdaptor extends RecyclerView.Adapter<VideoListAdaptor.ViewHolder> {

    private Context mContext;
    private ArrayList<VideoModel> mList;
    public VideoListAdaptor(Context context, ArrayList<VideoModel> list){
        mContext = context;
        mList = list;
    }

    @Override
    public VideoListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.video_list, parent, false);

        // Return a new holder instance
        VideoListAdaptor.ViewHolder viewHolder = new VideoListAdaptor.ViewHolder(contactView);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(VideoListAdaptor.ViewHolder holder, final int position) {

        final VideoModel contact = mList.get(position);

        // Set item views based on your views and data model
        TextView item_name = holder.name;
        TextView item_price = holder.duration;
        ImageView item_photo = holder.videoimage;
//        Button btn_action = holder.btn_action;

        item_name.setText(contact.getName());
        item_price.setText(contact.getDuration());

        System.out.println("videoimg"+contact.getImage());

        if (contact.getImage().isEmpty()) {
            holder.videoimage.setImageResource(R.drawable.defaultprofile);
        } else{

            Glide.with(mContext)
                    .load(contact.getImage())
                    .centerCrop()
                    .error(R.drawable.defaultprofile)
                    .placeholder(R.drawable.defaultprofile)
                    .into(holder.videoimage);

        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,ShowVideo.class);
                intent.putExtra("video_id",contact.getVideo());
                intent.putExtra("video_name",contact.getName());
                intent.putExtra("vid",contact.getId());
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
//        public ImageView item_image;
        public TextView name,duration;
        public ImageView videoimage;
        public LinearLayout relativeLayout;
//        protected Button btn_action;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = itemView.findViewById(R.id.name);
            duration = itemView.findViewById(R.id.duration);
            videoimage = itemView.findViewById(R.id.videoimage);
            relativeLayout = itemView.findViewById(R.id.lay);

        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {


        }
    }

}
