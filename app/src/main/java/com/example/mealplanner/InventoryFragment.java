package com.example.mealplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryFragment extends Fragment {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> headerList;
    private HashMap<String,List<String>> childMap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory,container,false);
        getActivity().setTitle("Inventory");
        listView = v.findViewById(R.id.inventory_expandablelist);
        headerList = new ArrayList<>();
        childMap = new HashMap<>();
        listAdapter = new ExpandableListAdapter(getContext(),headerList,childMap);
        prepareListData();
        listView.setAdapter(listAdapter);
        return v;
    }

    private void prepareListData() {
        headerList.add("Produce");
        headerList.add("Meat");
        headerList.add("Dairy");
        headerList.add("Grain");
        headerList.add("Sauce/Seasoning");
        headerList.add("Other (Snack, Beverage, etc.)");

        List<String> produce = new ArrayList<>();
        produce.add("spinach");
        produce.add("strawberries");
        List<String> meat = new ArrayList<>();
        meat.add("pork belly");
        meat.add("ground pork");
        meat.add("chicken breast");
        List<String> dairy = new ArrayList<>();
        dairy.add("milk");
        List<String> grain = new ArrayList<>();
        List<String> sauce = new ArrayList<>();
        sauce.add("pesto");
        List<String> other = new ArrayList<>();
        other.add("chips");
        other.add("orange juice");

        childMap.put(headerList.get(0), produce);
        childMap.put(headerList.get(1), meat);
        childMap.put(headerList.get(2), dairy);
        childMap.put(headerList.get(3), grain);
        childMap.put(headerList.get(4), sauce);
        childMap.put(headerList.get(5), other);

        listAdapter.notifyDataSetChanged();
    }
}
