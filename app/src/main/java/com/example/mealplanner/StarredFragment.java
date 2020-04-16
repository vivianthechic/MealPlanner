package com.example.mealplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarredFragment extends Fragment {

    private TextView textView;
    private RecyclerView recyclerView;
    private FirebaseFirestore fStore;
    private List<Recipe> recipeList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String recipe_ids;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_starred,container,false);
        getActivity().setTitle("Starred Recipes");
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipeList = new ArrayList<>();
        textView = v.findViewById(R.id.noStarred_textview);
        recyclerView = v.findViewById(R.id.starred_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),recipeList);
        recyclerView.setAdapter(recyclerViewAdapter);

        if(user != null){
            DocumentReference documentReference = fStore.collection("users").document(user.getUid());
            documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    recipe_ids = documentSnapshot.getString("starred");
                    if(recipe_ids.isEmpty()){
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        displayRecycler();
                    }
                }
            });
        }else{
            getActivity().finish();
            startActivity(new Intent(getContext(),Login.class));
        }
        return v;
    }

    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    private void displayRecycler(){
        List<String> r_ids = Arrays.asList(recipe_ids.split(","));
        for(int i = 0; i < r_ids.size(); i++) {
            final int id = Integer.parseInt(r_ids.get(i));
            DocumentReference documentReference = fStore.collection("recipes").document(String.valueOf(id));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("title");
                    Bitmap image = StringToBitMap(documentSnapshot.getString("image"));
                    String ingredients = documentSnapshot.getString("ingredients");
                    String instructions = documentSnapshot.getString("instructions");
                    recipeList.add(new Recipe(id, name, ingredients, instructions, image));
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
