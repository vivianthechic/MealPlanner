package com.example.mealplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {

    private ShopListAdapter produceAdapter, meatAdapter, dairyAdapter, grainAdapter, sauceAdapter, otherAdapter;
    private List<String> produceList, meatList, dairyList, grainList, sauceList, otherList;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Inventory");
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){finish();
            startActivity(new Intent(this,Login.class));
        }
        ListView pLV = findViewById(R.id.produce_listView);
        ListView mLV = findViewById(R.id.meat_listView);
        ListView dLV = findViewById(R.id.dairy_listView);
        ListView gLV = findViewById(R.id.grain_listView);
        ListView sLV = findViewById(R.id.sauce_listView);
        ListView oLV = findViewById(R.id.other_listView);
        produceList = new ArrayList<>(); meatList = new ArrayList<>();dairyList = new ArrayList<>(); grainList = new ArrayList<>(); sauceList = new ArrayList<>(); otherList = new ArrayList<>();
        prepareLists();
        produceAdapter = new ShopListAdapter(this,produceList,"Produce");
        meatAdapter = new ShopListAdapter(this,meatList,"Meat");
        dairyAdapter = new ShopListAdapter(this,dairyList,"Dairy");
        grainAdapter = new ShopListAdapter(this,grainList,"Grain");
        sauceAdapter = new ShopListAdapter(this,sauceList,"Sauce/Seasoning");
        otherAdapter = new ShopListAdapter(this,otherList,"Other");
        pLV.setAdapter(produceAdapter); mLV.setAdapter(meatAdapter); dLV.setAdapter(dairyAdapter); gLV.setAdapter(grainAdapter); sLV.setAdapter(sauceAdapter); oLV.setAdapter(otherAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.putExtra("frag",'3');
        finish();
        startActivityForResult(i, 0);
        return true;
    }

    private void prepareLists(){
        produceList.clear(); meatList.clear(); dairyList.clear(); grainList.clear(); sauceList.clear(); otherList.clear();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("inventory").whereEqualTo("uid",user.getUid()).whereEqualTo("onList",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < docs.size(); i++){
                    DocumentSnapshot doc = docs.get(i);
                    String name = doc.getString("item");
                    String category = doc.getString("category");
                    switch(category){
                        case "Produce": produceList.add(name); break;
                        case "Meat": meatList.add(name); break;
                        case "Dairy": dairyList.add(name); break;
                        case "Grain": grainList.add(name); break;
                        case "Sauce/Seasoning": sauceList.add(name); break;
                        default: otherList.add(name); break;
                    }
                }
                produceAdapter.notifyDataSetChanged(); meatAdapter.notifyDataSetChanged(); dairyAdapter.notifyDataSetChanged(); grainAdapter.notifyDataSetChanged(); sauceAdapter.notifyDataSetChanged(); otherAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clearGList(View view) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("inventory").whereEqualTo("uid",user.getUid()).whereEqualTo("onList",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < docs.size(); i++){
                    DocumentSnapshot doc = docs.get(i);
                    doc.getReference().update("onList",false);
                }
                produceList.clear(); meatList.clear(); dairyList.clear(); grainList.clear(); sauceList.clear(); otherList.clear();
                produceAdapter.notifyDataSetChanged(); meatAdapter.notifyDataSetChanged(); dairyAdapter.notifyDataSetChanged(); grainAdapter.notifyDataSetChanged(); sauceAdapter.notifyDataSetChanged(); otherAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Shopping list has been cleared.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
