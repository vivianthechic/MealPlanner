package com.example.mealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ShopListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mData;
    private String group;

    public ShopListAdapter(Context context, List<String> data, String group){
        super(context,R.layout.shop_list_item,data);
        this.mContext = context;
        this.mData = data;
        this.group = group;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.shop_list_item,parent,false);
        }
        final String itemText = mData.get(position);
        TextView childText = convertView.findViewById(R.id.shopList_item);
        childText.setText(itemText);
        ImageButton delete_item = convertView.findViewById(R.id.delete_shop_item);
        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromShoppingList(itemText);
                mData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private void deleteFromShoppingList(String item){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("inventory").whereEqualTo("uid",user.getUid()).whereEqualTo("item",item).whereEqualTo("category",group).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().get(0).getReference().update("onList",false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext,"Item deleted from shopping list.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
