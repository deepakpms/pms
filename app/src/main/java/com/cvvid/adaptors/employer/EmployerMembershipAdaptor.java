package com.cvvid.adaptors.employer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.activities.candidate.CandidateBasicRegister;
import com.cvvid.activities.employer.EmployerBasicRegister;
import com.cvvid.models.employer.EmployerMembershipModel;

import java.util.ArrayList;

public class EmployerMembershipAdaptor extends RecyclerView.Adapter<EmployerMembershipAdaptor.ViewHolder> {

    private Context mContext;
    private ArrayList<EmployerMembershipModel> mList;
    public EmployerMembershipAdaptor(Context context, ArrayList<EmployerMembershipModel> list){
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.can_mem_items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }



        @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            EmployerMembershipModel contact = mList.get(position);

        // Set item views based on your views and data model
        TextView item_name = holder.item_name;
        TextView item_price = holder.item_price;
        Button btn_action = holder.btn_action;

        item_name.setText(contact.getItem_name());
        item_price.setText(contact.getItem_price());

        if(position == 0){
            btn_action.setText("Basic Account");
        } else if(position == 1){
            btn_action.setText("Premium Account");
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
//        public ImageView item_image;
        public TextView item_name,item_price;
        protected Button btn_action;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            btn_action = itemView.findViewById(R.id.btn_action);
            btn_action.setOnClickListener(this);

//
//            item_image = itemView.findViewById(R.id.rv_item_img);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);

        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {

            if (getAdapterPosition() == 0){
                // navigate login activity
                Intent intent = new Intent(mContext ,EmployerBasicRegister.class);
                intent.putExtra("ispremium", false);
                mContext.startActivity(intent);
//                Toast.makeText(mContext, "Recycle Click" + "Basic", Toast.LENGTH_SHORT).show();
            } else if(getAdapterPosition() == 1){
                // navigate login activity
                Intent intent = new Intent(mContext ,CandidateBasicRegister.class);
                intent.putExtra("ispremium", true);
                mContext.startActivity(intent);
//                Toast.makeText(mContext, "Recycle Click" + "Premium", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
