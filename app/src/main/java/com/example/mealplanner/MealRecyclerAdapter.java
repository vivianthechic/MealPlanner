package com.example.mealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(@NonNull MealHolder holder, int position) {
        MealPlan mealPlan = mData.get(position);
        holder.date_text.setText(mealPlan.getMonth()+" "+mealPlan.getDay().substring(mealPlan.getDay().length()-2)+", "+mealPlan.getYear());
        holder.recipe_text.setText(mealPlan.getRecipeName());
        holder.notes_text.setText("Notes: "+mealPlan.getNotes());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MealHolder extends RecyclerView.ViewHolder{

        TextView date_text, recipe_text, notes_text;

        public MealHolder(@NonNull View itemView) {
            super(itemView);
            date_text = itemView.findViewById(R.id.mp_date);
            recipe_text = itemView.findViewById(R.id.mp_recipeName);
            notes_text = itemView.findViewById(R.id.mp_notes);
        }
    }
}
