package com.example.mealplanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class RecipeActivity extends AppCompatActivity {

    Button starBtn;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String recipeId, name, ingredients, instructions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView mRecipeName = findViewById(R.id.recipe_name_textview);
        TextView mIngredients = findViewById(R.id.ingredients_textview);
        TextView mInstructions = findViewById(R.id.instructions_textview);

        Intent i = getIntent();
        recipeId = i.getStringExtra("recipeId");
        name = i.getStringExtra("recipeName");
        ingredients = i.getStringExtra("ingredients");
        instructions = i.getStringExtra("instructions");

        mRecipeName.setText(name);
        mIngredients.setText(ingredients);
        mInstructions.setText(instructions);

        starBtn = findViewById(R.id.starRecipe_button);
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            DocumentReference documentReference = fStore.collection("users").document(user.getUid());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    List<String> recipe_ids = Arrays.asList(documentSnapshot.getString("starred").split(","));
                    if(recipe_ids.contains(recipeId)){
                        starBtn.setText("Remove from Starred Recipes");
                    }else{
                        starBtn.setText("Add to Starred Recipes");
                    }
                }
            });
        }else{
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(starBtn.getText().charAt(0) == 'R'){
                    removeStar();
                }else{
                    addStar();
                }
            }
        });
    }

    private void removeStar(){
        final DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> recipe_ids = new ArrayList<>(Arrays.asList(documentSnapshot.getString("starred").split(",")));
                recipe_ids.remove(recipeId);
                String new_ids;
                if(recipe_ids.size() == 0){
                    new_ids = "";
                }else{
                    new_ids = TextUtils.join(",",recipe_ids);
                }
                documentReference.update("starred",new_ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        starBtn.setText("Add to Starred Recipes");
                    }
                });
            }
        });
    }

    private void addStar(){
        final DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String r_ids = documentSnapshot.getString("starred");
                String new_ids;
                if(r_ids == ""){
                    new_ids = recipeId;
                } else {
                    List<String> recipe_ids = new ArrayList<>(Arrays.asList(documentSnapshot.getString("starred").split(",")));
                    recipe_ids.add(recipeId);
                    new_ids = TextUtils.join(",",recipe_ids);
                }
                documentReference.update("starred",new_ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        starBtn.setText("Remove from Starred Recipes");
                    }
                });
            }
        });
    }
}
