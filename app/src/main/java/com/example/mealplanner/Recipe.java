package com.example.mealplanner;

import android.graphics.Bitmap;

public class Recipe {

    private int recipeId;
    private String recipeName;
    private String recipeIngredients;
    private Bitmap recipeImage;
    private String recipeInstructions;

    public Recipe(int id, String name, String ingredients, String instructions, Bitmap image){
        recipeId = id;
        recipeName = name;
        recipeIngredients = ingredients;
        recipeImage = image;
        recipeInstructions = instructions;
    }

    public int getRecipeId(){ return recipeId; }
    public String getRecipeName(){ return recipeName; }
    public String getRecipeIngredients(){ return recipeIngredients; }
    public String getRecipeInstructions(){ return recipeInstructions; }
    public Bitmap getRecipeImage(){ return recipeImage; }
}
