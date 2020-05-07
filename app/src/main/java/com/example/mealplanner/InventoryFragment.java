package com.example.mealplanner;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private ExpandableListAdapter listAdapter;
    private List<String> headerList, produce, meat, dairy, grain, sauce, other;
    private HashMap<String,List<String>> childMap;
    private AlertDialog alertDialog;
    private String selected_group;
    private FirebaseUser user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inventory,container,false);
        getActivity().setTitle("Inventory");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            getActivity().finish();
            startActivity(new Intent(getContext(),Login.class));
        }
        ExpandableListView listView = v.findViewById(R.id.inventory_expandablelist);
        headerList = Arrays.asList("Produce","Meat","Dairy","Grain","Sauce/Seasoning","Other (Snack, Drink, etc.)");
        childMap = new HashMap<>();
        produce = new ArrayList<>(); meat = new ArrayList<>(); dairy = new ArrayList<>(); grain = new ArrayList<>(); sauce = new ArrayList<>(); other = new ArrayList<>();
        prepareListData();
        listAdapter = new ExpandableListAdapter(getContext(),headerList,childMap);
        listView.setAdapter(listAdapter);

        ImageButton addInvBtn = v.findViewById(R.id.add_to_inventory);
        ImageButton showGList = v.findViewById(R.id.show_groceries);

        addInvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                View addView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_add,null);
                Spinner spinner = addView.findViewById(R.id.inventory_group_spinner);
                List<String> group_list = Arrays.asList("Produce","Meat","Dairy","Grain","Sauce/Seasoning","Other");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,group_list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                selected_group = "Produce";
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected_group = (String) parent.getItemAtPosition(position);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                final EditText item_et = addView.findViewById(R.id.inventory_item_text);
                Button addBtn = addView.findViewById(R.id.add_inventory_button);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String item_name = item_et.getText().toString();
                        saveInventoryItem(item_name,selected_group);
                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        showGList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    private void saveInventoryItem(final String item_name, final String food_group){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        final CollectionReference inventoryCollection = fStore.collection("inventory");
        inventoryCollection.whereEqualTo("item",item_name).whereEqualTo("category",food_group).whereEqualTo("uid",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().isEmpty()){
                    Map<String,Object> data = new HashMap<>();
                    data.put("item",item_name);
                    data.put("category",food_group);
                    data.put("onList",false);
                    data.put("uid",user.getUid());
                    inventoryCollection.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(),"Item added to inventory.",Toast.LENGTH_SHORT).show();
                            prepareListData();
                        }
                    });
                }else{
                    Toast.makeText(getContext(),"Item already in inventory.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void prepareListData() {
        produce.clear(); meat.clear(); dairy.clear(); grain.clear(); sauce.clear(); other.clear(); childMap.clear();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        CollectionReference inventoryCollection = fStore.collection("inventory");
        inventoryCollection.whereEqualTo("uid",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < docs.size(); i++) {
                    DocumentSnapshot doc = docs.get(i);
                    String name = doc.getString("item");
                    String category = doc.getString("category");
                    switch(category){
                        case "Produce": produce.add(name); break;
                        case "Meat": meat.add(name); break;
                        case "Dairy": dairy.add(name); break;
                        case "Grain": grain.add(name); break;
                        case "Sauce/Seasoning": sauce.add(name); break;
                        default: other.add(name); break;
                    }
                }
                childMap.put(headerList.get(0), produce);
                childMap.put(headerList.get(1), meat);
                childMap.put(headerList.get(2), dairy);
                childMap.put(headerList.get(3), grain);
                childMap.put(headerList.get(4), sauce);
                childMap.put(headerList.get(5), other);

                listAdapter.notifyDataSetChanged();
            }
        });
    }
}
