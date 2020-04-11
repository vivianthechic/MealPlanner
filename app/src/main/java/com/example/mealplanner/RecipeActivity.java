package com.example.mealplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView mRecipeName = findViewById(R.id.recipe_name_textview);
        TextView mIngredients = findViewById(R.id.ingredients_textview);
        TextView mInstructions = findViewById(R.id.instructions_textview);

        Intent i = getIntent();
        String name = i.getStringExtra("recipeName");
        String ingredients = i.getStringExtra("ingredients");
        String instructions = i.getStringExtra("instructions");

        mRecipeName.setText(name);
        mIngredients.setText(ingredients);
        mInstructions.setText(instructions);
    }
}
