package com.example.mealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mHeaderData;
    private HashMap<String,List<String>> mChildData;

    public ExpandableListAdapter(Context mContext, List<String> mHeaderData, HashMap<String, List<String>> mChildData) {
        this.mContext = mContext;
        this.mHeaderData = mHeaderData;
        this.mChildData = mChildData;
    }

    @Override
    public int getGroupCount() {
        return this.mHeaderData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildData.get(this.mHeaderData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mHeaderData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mChildData.get(this.mHeaderData.get(groupPosition)).get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        View v = convertView;
        if(v == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            v = layoutInflater.inflate(R.layout.inventory_list_group,parent,false);
        }
        switch (groupPosition){
            case 0: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_green)); break;
            case 1: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_yellow)); break;
            case 2: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_orange)); break;
            case 3: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_red)); break;
            case 4: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_purple)); break;
            default: v.setBackgroundColor(mContext.getResources().getColor(R.color.p_blue));break;
        }

        TextView headerText = v.findViewById(R.id.expandablelist_title);
        headerText.setText(title);

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String itemText = (String) getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.inventory_list_item,parent,false);
        }
        TextView childText = convertView.findViewById(R.id.expandablelist_item);
        childText.setText(itemText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
