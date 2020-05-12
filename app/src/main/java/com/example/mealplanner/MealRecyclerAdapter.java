package com.example.mealplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MealRecyclerAdapter extends RecyclerView.Adapter<MealRecyclerAdapter.MealHolder> {

    private Context mContext;
    private List<MealPlan> mData;

    public MealRecyclerAdapter(Context context, List<MealPlan> list) {
        this.mContext = context;
        this.mData = list;
    }

    @NonNull
    @Override
    public MealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.cardview_mealplan,parent,false);
        return new MealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealHolder holder, final int position) {
        final MealPlan mealPlan = mData.get(position);
        holder.date_text.setText(mealPlan.getMonth()+" "+mealPlan.getDay().substring(mealPlan.getDay().length()-2)+", "+mealPlan.getYear());
        holder.recipe_text.setText(mealPlan.getRecipeName());
        holder.notes_text.setText("Notes: "+mealPlan.getNotes());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMealPlan(mealPlan.getRecipeId(),mealPlan.getNotes(),mealPlan.getDay());
                mData.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.recipe_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecipe(mData.get(position).getRecipeId(),mData.get(position).getRecipeName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MealHolder extends RecyclerView.ViewHolder{

        TextView date_text, recipe_text, notes_text;
        ImageButton deleteBtn;

        public MealHolder(@NonNull View itemView) {
            super(itemView);
            date_text = itemView.findViewById(R.id.mp_date);
            recipe_text = itemView.findViewById(R.id.mp_recipeName);
            notes_text = itemView.findViewById(R.id.mp_notes);
            deleteBtn = itemView.findViewById(R.id.mp_delete);
        }
    }

    private void deleteMealPlan(String recipeId, String notes, String day){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            CollectionReference mealPlanCollection = fStore.collection("mealPlans");
            mealPlanCollection.whereEqualTo("recipeId",recipeId).whereEqualTo("day",day).whereEqualTo("notes",notes)
                    .whereEqualTo("uid",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    queryDocumentSnapshots.getDocuments().get(0).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            });
        }
    }

    private void showRecipe(final String recipeId, final String recipeName){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("recipes").document(recipeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String ing = documentSnapshot.getString("ingredients");
                String ins = documentSnapshot.getString("instructions");
                Intent i = new Intent(mContext,RecipeActivity.class);
                i.putExtra("recipeId",recipeId);
                i.putExtra("recipeName",recipeName);
                i.putExtra("ingredients",ing);
                i.putExtra("instructions",ins);
                mContext.startActivity(i);
            }
        });
    }
}
