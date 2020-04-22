package com.example.mealplanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.recipeTitle.setText(mData.get(position).getRecipeName());
        holder.recipeImage.setImageBitmap(mData.get(position).getRecipeImage());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                final DocumentReference recipeDoc = fStore.collection("recipes").document(String.valueOf(mData.get(position).getRecipeId()));
                recipeDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            Map<String,Object> r = new HashMap<>();
                            r.put("title",mData.get(position).getRecipeName());
                            r.put("ingredients",mData.get(position).getRecipeIngredients());
                            r.put("instructions",mData.get(position).getRecipeInstructions());
                            r.put("image",BitMapToString(mData.get(position).getRecipeImage()));
                            recipeDoc.set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }
                    }
                });

                Intent i = new Intent(mContext,RecipeActivity.class);
                i.putExtra("recipeId",String.valueOf(mData.get(position).getRecipeId()));
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
