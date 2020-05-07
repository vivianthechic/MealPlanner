package com.example.mealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

        TextView numItems = v.findViewById(R.id.num_items);
        List<String> list = mChildData.get(mHeaderData.get(groupPosition));
        if(list == null || list.isEmpty()){
            numItems.setText("0 items");
        }else{
            String x = list.size()+" items";
            numItems.setText(x);
        }

        return v;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String itemText = (String) getChild(groupPosition,childPosition);
        final String group = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.inventory_list_item,parent,false);
        }
        TextView childText = convertView.findViewById(R.id.expandablelist_item);
        childText.setText(itemText);
        ImageButton add_to_shop = convertView.findViewById(R.id.add_to_shoplist);
        ImageButton delete_item = convertView.findViewById(R.id.delete_inv_item);
        add_to_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToShoppingList(itemText,group);
            }
        });
        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromInventory(itemText,group);
                mChildData.get(group).remove(childPosition);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void addItemToShoppingList(String item, String group){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("inventory").whereEqualTo("uid",user.getUid()).whereEqualTo("item",item).whereEqualTo("category",group).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < docs.size();i++){
                    DocumentSnapshot doc = docs.get(i);
                    doc.getReference().update("onList",true);
                    Toast.makeText(mContext,"Item added to shopping list.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteFromInventory(String item, String group){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("inventory").whereEqualTo("uid",user.getUid()).whereEqualTo("item",item).whereEqualTo("category",group).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().get(0).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext,"Item deleted from inventory.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
