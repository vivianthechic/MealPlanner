package com.example.mealplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {

    private Context mContext;
    private List<Recipe> mData;

    public RecyclerViewAdapter(Context context, List<Recipe> list){
        this.mContext = context;
        this.mData = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.cardview_recipe,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.recipeTitle.setText(mData.get(position).getRecipeName());
        holder.recipeImage.setImageBitmap(mData.get(position).getRecipeImage());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,RecipeActivity.class);
                i.putExtra("recipeName",mData.get(position).getRecipeName());
                i.putExtra("ingredients",mData.get(position).getRecipeIngredients());
                i.putExtra("instructions",mData.get(position).getRecipeInstructions());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage;
        CardView cardView;
        public MyHolder(View itemView){
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipecard_textview);
            recipeImage = itemView.findViewById(R.id.recipecard_image);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
