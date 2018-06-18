package com.cvvid.adaptors.employer;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.cvvid.R;
import com.cvvid.models.employer.ApplicationChildItem;
import com.cvvid.models.employer.ApplicationGroupItemsInfo;

import java.util.ArrayList;

public class CustomExpandableApplicationListAdapter  implements ExpandableListAdapter {

    private Context context;
    private ArrayList<ApplicationGroupItemsInfo> teamName;

    public CustomExpandableApplicationListAdapter(Context context, ArrayList<ApplicationGroupItemsInfo> deptList) {
        this.context = context;
        this.teamName = deptList;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return teamName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ApplicationChildItem> productList = teamName.get(groupPosition).getList();
        return productList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return teamName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ApplicationChildItem> productList = teamName.get(groupPosition).getList();
        return productList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ApplicationGroupItemsInfo headerInfo = (ApplicationGroupItemsInfo) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.applicationslist_group, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.listTitle);
        heading.setText(headerInfo.getName().trim());
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ApplicationChildItem detailInfo = (ApplicationChildItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.applicationslist_items, null);
        }
        System.out.println("----------EMILA--"+detailInfo.getEmail());

        TextView email = (TextView) convertView.findViewById(R.id.itemEmail);
        email.setText(detailInfo.getEmail().trim());
        TextView status = (TextView) convertView.findViewById(R.id.itemStatus);
        status.setText(detailInfo.getStatus().trim());
        TextView date = (TextView) convertView.findViewById(R.id.itemDate);
        date.setText(detailInfo.getDate().trim());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}